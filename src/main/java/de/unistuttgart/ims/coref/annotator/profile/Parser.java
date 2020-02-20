package de.unistuttgart.ims.coref.annotator.profile;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.unistuttgart.ims.coref.annotator.Annotator;

public class Parser {
	public Profile getProfile(File file) throws JAXBException {
		JAXBContext jaxbContext;
		jaxbContext = JAXBContext.newInstance(Profile.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Profile profile = (Profile) jaxbUnmarshaller.unmarshal(file);
		return profile;
	}

	public Profile getProfileOrNull(File file) {
		try {
			if (file.exists() && file.canRead() && file.isFile())
				return getProfile(file);
		} catch (JAXBException e) {
			Annotator.logger.catching(e);
		}
		return null;
	}
}
