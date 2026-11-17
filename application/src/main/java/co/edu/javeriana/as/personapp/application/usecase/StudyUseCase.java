package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.domain.Study;

@UseCase
public class StudyUseCase implements StudyInputPort {
	private final StudyOutputPort output;

	public StudyUseCase(@Qualifier("studyOutputAdapterMaria") StudyOutputPort output) {
		this.output = output;
	}

	@Override
	public Study create(Study study) {
		validate(study);
		if (output.findById(study.getPerson().getIdentification(),
				study.getProfession().getIdentification()) != null) {
			throw new AlreadyExistsException("Study already exists for this person and profession");
		}
		return output.save(study);
	}

	@Override
	public Study edit(Integer personIdentification, Integer professionIdentification, Study study)
			throws NoExistException {
		if (output.findById(personIdentification, professionIdentification) == null) {
			throw new NoExistException("Study does not exist");
		}
		validate(study);
		study.getPerson().setIdentification(personIdentification);
		study.getProfession().setIdentification(professionIdentification);
		return output.save(study);
	}

	@Override
	public Boolean drop(Integer personIdentification, Integer professionIdentification) throws NoExistException {
		if (output.findById(personIdentification, professionIdentification) == null) {
			throw new NoExistException("Study does not exist");
		}
		return output.delete(personIdentification, professionIdentification);
	}

	@Override
	public List<Study> findAll() {
		return output.find();
	}

	@Override
	public Study findOne(Integer personIdentification, Integer professionIdentification) throws NoExistException {
		Study study = output.findById(personIdentification, professionIdentification);
		if (study == null) {
			throw new NoExistException("Study does not exist");
		}
		return study;
	}

	@Override
	public Integer count() {
		return output.count();
	}

	private void validate(Study study) {
		if (study == null || study.getPerson() == null || study.getPerson().getIdentification() == null
				|| study.getProfession() == null || study.getProfession().getIdentification() == null
				|| study.getUniversityName() != null && study.getUniversityName().length() > 50) {
			throw new IllegalArgumentException("Study requires person and profession ids; university name is at most 50 characters");
		}
	}
}
