package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelefonoResponse extends TelefonoRequest {
	private String status;

	public TelefonoResponse(String number, String company, Integer ownerIdentification, String database, String status) {
		super(number, company, ownerIdentification, database);
		this.status = status;
	}
}
