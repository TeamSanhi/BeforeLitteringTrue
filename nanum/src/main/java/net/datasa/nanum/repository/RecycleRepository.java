package net.datasa.nanum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.nanum.domain.entity.RecycleEntity;

@Repository
public interface RecycleRepository  extends JpaRepository<RecycleEntity, Integer> {


}
