package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mongo.mapper.ProfesionMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.ProfesionRepositoryMongo;

@Adapter("professionOutputAdapterMongo")
public class ProfessionOutputAdapterMongo implements ProfessionOutputPort {
	@Autowired private ProfesionRepositoryMongo repository;
	@Autowired private EstudiosRepositoryMongo studyRepository;
	@Autowired private ProfesionMapperMongo mapper;

	@Override
	public Profession save(Profession profession) {
		return mapper.fromAdapterToDomain(repository.save(mapper.fromDomainToAdapter(profession)));
	}

	@Override
	public Boolean delete(Integer identification) {
		studyRepository.deleteByProfesion(identification);
		repository.deleteById(identification);
		return !repository.existsById(identification);
	}

	@Override
	public List<Profession> find() {
		return repository.findAll().stream().map(mapper::fromAdapterToDomain).collect(Collectors.toList());
	}

	@Override
	public Profession findById(Integer identification) {
		return repository.findById(identification).map(mapper::fromAdapterToDomain).orElse(null);
	}

	@Override
	public Integer count() {
		return Math.toIntExact(repository.count());
	}
}
