package co.edu.javeriana.as.personapp.terminal.adapter;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;

@Adapter
public class CrudInputAdapterCli {
	private final PersonOutputPort personMaria;
	private final PersonOutputPort personMongo;
	private final ProfessionOutputPort professionMaria;
	private final ProfessionOutputPort professionMongo;
	private final PhoneOutputPort phoneMaria;
	private final PhoneOutputPort phoneMongo;
	private final StudyOutputPort studyMaria;
	private final StudyOutputPort studyMongo;

	public CrudInputAdapterCli(
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personMaria,
			@Qualifier("personOutputAdapterMongo") PersonOutputPort personMongo,
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionMaria,
			@Qualifier("professionOutputAdapterMongo") ProfessionOutputPort professionMongo,
			@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phoneMaria,
			@Qualifier("phoneOutputAdapterMongo") PhoneOutputPort phoneMongo,
			@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyMaria,
			@Qualifier("studyOutputAdapterMongo") StudyOutputPort studyMongo) {
		this.personMaria = personMaria;
		this.personMongo = personMongo;
		this.professionMaria = professionMaria;
		this.professionMongo = professionMongo;
		this.phoneMaria = phoneMaria;
		this.phoneMongo = phoneMongo;
		this.studyMaria = studyMaria;
		this.studyMongo = studyMongo;
	}

	public PersonInputPort person(String database) {
		return new PersonUseCase(isMaria(database) ? personMaria : personMongo);
	}

	public ProfessionInputPort profession(String database) {
		return new ProfessionUseCase(isMaria(database) ? professionMaria : professionMongo);
	}

	public PhoneInputPort phone(String database) {
		return new PhoneUseCase(isMaria(database) ? phoneMaria : phoneMongo);
	}

	public StudyInputPort study(String database) {
		return new StudyUseCase(isMaria(database) ? studyMaria : studyMongo);
	}

	private boolean isMaria(String database) {
		if (DatabaseOption.MARIA.name().equalsIgnoreCase(database)) {
			return true;
		}
		if (DatabaseOption.MONGO.name().equalsIgnoreCase(database)) {
			return false;
		}
		throw new IllegalArgumentException("Database must be MARIA or MONGO");
	}
}
