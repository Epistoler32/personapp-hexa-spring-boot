package co.edu.javeriana.as.personapp.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;

public interface EstudiosRepositoryMongo extends MongoRepository<EstudiosDocument, String> {

	@Query(value = "{ 'primaryPersona' : ?0 }", delete = true)
	void deleteByPersona(Integer cc);

	@Query(value = "{ 'primaryProfesion' : ?0 }", delete = true)
	void deleteByProfesion(Integer id);
}
