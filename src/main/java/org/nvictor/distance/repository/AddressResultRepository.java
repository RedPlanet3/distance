package org.nvictor.distance.repository;

import org.nvictor.distance.model.AddressResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressResultRepository extends JpaRepository<AddressResult, Long> {
}
