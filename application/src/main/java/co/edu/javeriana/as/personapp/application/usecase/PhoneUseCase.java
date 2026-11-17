package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;

@UseCase
public class PhoneUseCase implements PhoneInputPort {
	private final PhoneOutputPort output;

	public PhoneUseCase(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort output) {
		this.output = output;
	}

	@Override
	public Phone create(Phone phone) {
		validate(phone);
		if (output.findById(phone.getNumber()) != null) {
			throw new AlreadyExistsException("Phone " + phone.getNumber() + " already exists");
		}
		return output.save(phone);
	}

	@Override
	public Phone edit(String number, Phone phone) throws NoExistException {
		if (output.findById(number) == null) {
			throw new NoExistException("Phone " + number + " does not exist");
		}
		validate(phone);
		phone.setNumber(number);
		return output.save(phone);
	}

	@Override
	public Boolean drop(String number) throws NoExistException {
		if (output.findById(number) == null) {
			throw new NoExistException("Phone " + number + " does not exist");
		}
		return output.delete(number);
	}

	@Override
	public List<Phone> findAll() {
		return output.find();
	}

	@Override
	public Phone findOne(String number) throws NoExistException {
		Phone phone = output.findById(number);
		if (phone == null) {
			throw new NoExistException("Phone " + number + " does not exist");
		}
		return phone;
	}

	@Override
	public Integer count() {
		return output.count();
	}

	@Override
	public Person getOwner(String number) throws NoExistException {
		return findOne(number).getOwner();
	}

	private void validate(Phone phone) {
		if (phone == null || phone.getNumber() == null || phone.getNumber().trim().isEmpty()
				|| phone.getNumber().length() > 15 || phone.getCompany() == null
				|| phone.getCompany().trim().isEmpty() || phone.getCompany().length() > 45
				|| phone.getOwner() == null || phone.getOwner().getIdentification() == null) {
			throw new IllegalArgumentException("Phone requires number, company, and owner identification");
		}
	}
}
