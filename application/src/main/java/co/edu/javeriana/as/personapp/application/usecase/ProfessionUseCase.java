package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.domain.Profession;

@UseCase
public class ProfessionUseCase implements ProfessionInputPort {
	private final ProfessionOutputPort output;

	public ProfessionUseCase(@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort output) {
		this.output = output;
	}

	@Override
	public Profession create(Profession profession) {
		validate(profession);
		if (output.findById(profession.getIdentification()) != null) {
			throw new AlreadyExistsException("Profession " + profession.getIdentification() + " already exists");
		}
		return output.save(profession);
	}

	@Override
	public Profession edit(Integer identification, Profession profession) throws NoExistException {
		if (output.findById(identification) == null) {
			throw new NoExistException("Profession " + identification + " does not exist");
		}
		validate(profession);
		profession.setIdentification(identification);
		return output.save(profession);
	}

	@Override
	public Boolean drop(Integer identification) throws NoExistException {
		if (output.findById(identification) == null) {
			throw new NoExistException("Profession " + identification + " does not exist");
		}
		return output.delete(identification);
	}

	@Override
	public List<Profession> findAll() {
		return output.find();
	}

	@Override
	public Profession findOne(Integer identification) throws NoExistException {
		Profession profession = output.findById(identification);
		if (profession == null) {
			throw new NoExistException("Profession " + identification + " does not exist");
		}
		return profession;
	}

	@Override
	public Integer count() {
		return output.count();
	}

	private void validate(Profession profession) {
		if (profession == null || profession.getIdentification() == null || profession.getName() == null
				|| profession.getName().trim().isEmpty() || profession.getName().length() > 90) {
			throw new IllegalArgumentException("Profession requires an id and a name of at most 90 characters");
		}
	}
}
