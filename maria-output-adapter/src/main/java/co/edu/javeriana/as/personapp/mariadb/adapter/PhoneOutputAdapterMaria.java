package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.TelefonoMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.TelefonoRepositoryMaria;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("phoneOutputAdapterMaria")
@Transactional
public class PhoneOutputAdapterMaria implements PhoneOutputPort {
	@Autowired private TelefonoRepositoryMaria repository;
	@Autowired private PersonaRepositoryMaria personRepository;
	@Autowired private TelefonoMapperMaria mapper;

	@Override
	public Phone save(Phone phone) {
		if (phone.getOwner() == null || !personRepository.existsById(phone.getOwner().getIdentification())) {
			throw new IllegalArgumentException("Phone owner does not exist");
		}
		TelefonoEntity mapped = mapper.fromDomainToAdapter(phone);
		mapped.setDuenio(personRepository.getReferenceById(phone.getOwner().getIdentification()));
		TelefonoEntity persisted = repository.save(mapped);
		return mapper.fromAdapterToDomain(persisted);
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
