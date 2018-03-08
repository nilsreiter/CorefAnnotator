package de.unistuttgart.ims.coref.annotator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateCheck {

	private static final String HTML_URL = "html_url";
	private static final String TAG_NAME = "tag_name";
	transient JSONObject cache = null;
	long lastCheck = Long.MIN_VALUE;

	public boolean checkForUpdate() throws IOException {
		Version current = Version.get();

		JSONObject o;
		o = getRemote();
		String tagName = o.getString(TAG_NAME);
		Version remote = Version.get(tagName.substring(1));
		return current.compareTo(remote) < 0;
	}

	public Version getRemoteVersion() throws IOException {
		JSONObject o = getRemote();
		String tagName = o.getString(TAG_NAME);
		return Version.get(tagName.substring(1));
	}

	public URI getReleasePage() throws IOException {
		JSONObject o = getRemote();
		try {
			return new URI(o.getString(HTML_URL));
		} catch (JSONException | URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected JSONObject getRemote() throws IOException {
		long current = System.currentTimeMillis();
		if ((current - lastCheck) > 1000 || cache == null) {
			URL url = new URL(Constants.URL_LATEST_RELEASE_API);
			String s = IOUtils.toString(url.openStream(), "UTF-8");
			cache = new JSONObject(s);
			lastCheck = current;
			return cache;
		} else
			return cache;
	}

	public static class Version implements Comparable<Version> {
		private static final String DEFAULT_VERSION = "0.0.1-SNAPSHOT";

		private enum Tag {
			ALPHA, BETA, SNAPSHOT
		};

		static Pattern numbers = Pattern.compile("\\d+");
		static Pattern characters = Pattern.compile("[a-zA-Z]+");

		int major = 0;
		int minor = 0;
		int patch = 0;
		String tagString = null;
		int tagLevel = 0;
		Tag tag = null;

		static Version version = null;

		private Version(String versionString) {
			String[] parts = versionString.split("[\\.-]");
			major = Integer.parseInt(parts[0]);
			minor = Integer.parseInt(parts[1]);
			patch = Integer.parseInt(parts[2]);
			if (parts.length > 3) {
				tagString = parts[3];
				Matcher m = numbers.matcher(parts[3]);
				if (m.find())
					tagLevel = Integer.parseInt(m.group());
				m = characters.matcher(parts[3]);
				if (m.find())
					tag = Tag.valueOf(m.group().toUpperCase());

			}
		}

		public static Version get(String s) {
			return new Version(s);
		}

		public static Version get() {
			if (version == null) {
				String s = Annotator.class.getPackage().getImplementationVersion();
				if (s == null)
					s = DEFAULT_VERSION;
				version = new Version(s);
			}
			return version;
		}

		@Override
		public int compareTo(Version o) {
			int r = Integer.compare(this.major, o.major);
			if (r != 0)
				return r;
			r = Integer.compare(this.minor, o.minor);
			if (r != 0)
				return r;
			r = Integer.compare(this.patch, o.patch);
			if (r != 0)
				return r;
			if (this.tag != null && o.tag == null)
				return -1;
			if (this.tag == null && o.tag != null)
				return 1;
			if (this.tag == null && o.tag == null)
				return 0;
			r = this.tag.compareTo(o.tag);
			if (r != 0)
				return r;
			r = Integer.compare(this.tagLevel, o.tagLevel);
			if (r != 0)
				return r;

			return 0;
		}

		@Override
		public int hashCode() {
			return toString().hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return toString().equals(o.toString());
		}

		@Override
		public String toString() {
			return major + "." + minor + "." + patch + (tagString != null ? "-" + tagString : "");
		}

	}
}
