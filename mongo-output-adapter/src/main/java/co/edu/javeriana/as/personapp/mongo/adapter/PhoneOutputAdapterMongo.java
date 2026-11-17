package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.TelefonoMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.PersonaRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.TelefonoRepositoryMongo;

@Adapter("phoneOutputAdapterMongo")
public class PhoneOutputAdapterMongo implements PhoneOutputPort {
	@Autowired private TelefonoRepositoryMongo repository;
	@Autowired private PersonaRepositoryMongo personRepository;
	@Autowired private TelefonoMapperMongo mapper;

	@Override
	public Phone save(Phone phone) {
		if (phone.getOwner() == null || phone.getOwner().getIdentification() == null) {
			throw new IllegalArgumentException("Phone owner does not exist");
		}
		PersonaDocument owner = personRepository.findById(phone.getOwner().getIdentification())
				.orElseThrow(() -> new IllegalArgumentException("Phone owner does not exist"));
		TelefonoDocument document = mapper.fromDomainToAdapter(phone);
		document.setPrimaryDuenio(owner);
		return mapper.fromAdapterToDomain(repository.save(document));
	}

	@Override
	public Boolean delete(String number) {
		repository.deleteById(number);
		return !repository.existsById(number);
	}

	@Override
	public List<Phone> find() {
		return repository.findAll().stream().map(mapper::fromAdapterToDomain).collect(Collectors.toList());
	}

	@Override
	public Phone findById(String number) {
		return repository.findById(number).map(mapper::fromAdapterToDomain).orElse(null);
	}

	@Override
	public Integer count() {
		return Math.toIntExact(repository.count());
	}
}
