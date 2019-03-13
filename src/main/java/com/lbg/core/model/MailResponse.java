package com.lbg.core.model;

import org.springframework.stereotype.Component;

@Component
public class MailResponse {
	private String mailresponse;

	@Override
	public String toString() {
		return "MailResponse [mailresponse=" + mailresponse + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mailresponse == null) ? 0 : mailresponse.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailResponse other = (MailResponse) obj;
		if (mailresponse == null) {
			if (other.mailresponse != null)
				return false;
		} else if (!mailresponse.equals(other.mailresponse))
			return false;
		return true;
	}

	public String getMailresponse() {
		return mailresponse;
	}

	public void setMailresponse(String mailresponse) {
		this.mailresponse = mailresponse;
	}
}
