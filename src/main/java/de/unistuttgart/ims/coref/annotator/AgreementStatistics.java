package de.unistuttgart.ims.coref.annotator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.unistuttgart.ims.coref.annotator.comp.CABean;

public class AgreementStatistics implements CABean {
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	int agreed = 0;
	double agreementInSpan = 0;
	int total = 0;
	int totalInOverlappingPart = 0;

	public String total() {
		return String.valueOf(total);
	}

	public String agreed() {
		return String.valueOf(agreed);
	}

	public int getAgreed() {
		return agreed;
	}

	public void setAgreed(int agreed) {
		int oldValue = this.agreed;
		this.agreed = agreed;
		pcs.firePropertyChange("agreed", oldValue, agreed);
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		int oldTotal = this.total;
		this.total = total;
		pcs.firePropertyChange("total", oldTotal, total);

	}

	public int getTotalInOverlappingPart() {
		return totalInOverlappingPart;
	}

	public void setTotalInOverlappingPart(int totalInOverlappingPart) {
		int oldValue = this.totalInOverlappingPart;
		this.totalInOverlappingPart = totalInOverlappingPart;
		pcs.firePropertyChange("totalInOverlappingPart", oldValue, totalInOverlappingPart);
	}

	public double getAgreementInSpan() {
		return agreementInSpan;
	}

	public void setAgreementInSpan(double agreementInSpan) {
		double oldAgreement = this.agreementInSpan;
		this.agreementInSpan = agreementInSpan;
		pcs.firePropertyChange("agreementInSpan", oldAgreement, agreementInSpan);
	}
}