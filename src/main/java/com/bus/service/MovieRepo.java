package com.bus.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bus.beans.MovieDetails;

@Repository
public interface MovieRepo extends JpaRepository<MovieDetails, Long> {
	
	@Query("SELECT m FROM MovieDetails m WHERE m.movieName = :movieName")
	MovieDetails findByMovieName(String movieName);

}
