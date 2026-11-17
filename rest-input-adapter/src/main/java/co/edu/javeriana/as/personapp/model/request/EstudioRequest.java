package co.edu.javeriana.as.personapp.model.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioRequest {
	private Integer personIdentification;
	private Integer professionIdentification;
	private LocalDate graduationDate;
	private String universityName;
	private String database;
}
