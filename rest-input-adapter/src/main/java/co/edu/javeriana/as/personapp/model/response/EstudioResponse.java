package co.edu.javeriana.as.personapp.model.response;

import java.time.LocalDate;

import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstudioResponse extends EstudioRequest {
	private String status;

	public EstudioResponse(Integer personIdentification, Integer professionIdentification, LocalDate graduationDate,
			String universityName, String database, String status) {
		super(personIdentification, professionIdentification, graduationDate, universityName, database);
		this.status = status;
	}
}
