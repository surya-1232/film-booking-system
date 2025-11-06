package com.bus.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bus.beans.CurrentDateOperation;
import com.bus.beans.Customer;
import com.bus.beans.MovieDetails;
import com.bus.beans.OrderHistory;
import com.bus.beans.SalesReport;
import com.bus.beans.Seat;

@Component
public class CustomerDao {
	
	@Autowired private CustomerRepo customerRepo;
	@Autowired private SeatRepo seatRepo;
	@Autowired private HistoryRepo historyRepo;
	@Autowired private MovieRepo movieRepo;
	@Autowired private SalesRepo salesRepo;
	
	public Customer save(Customer customer) {
		return customerRepo.save(customer);
	}
	
//	@Cacheable(cacheNames = "login", key = "'customer'+#email+#password")
	public Customer login(String email, String password) {
		Customer customer = customerRepo.findByEmailAndPassword(email, password);
		return customer;
	}
	
	@Transactional
	public Seat saveSeat(Seat seat, Customer customer, Date date, String time){
		
		if(customer.getSeat() == null) {
			customer.setSeat(new ArrayList<>());
		}
		customer.getSeat().add(seat);
		
		CurrentDateOperation cdo= new CurrentDateOperation();
		cdo.setShowDate(date);
		cdo.setShowTime(time);
		cdo.setSeat(List.of(seat));
		
		seat.setOperation(cdo);
		seat.setCustomer(customer);
		Seat save = seatRepo.save(seat);
		return save;
	}
	
	public List<Seat> getSeats(long id){
		List<Seat> list = seatRepo.getAllSeat(id);
		return list;
	}
	
	public List<Customer> getAll(){
		List<Customer> findAll = customerRepo.findAll();
		return findAll;
	}
	
	public List<MovieDetails> getAllMovies() {
		List<MovieDetails> movies = movieRepo.findAll();
		return movies;
	}
	
	@Transactional
	public OrderHistory saveHistory(OrderHistory history, Customer customer) {
		if(customer.getHistory() == null) {
			customer.setHistory(history);
		}
		history.setCustomer(customer);
		OrderHistory save = historyRepo.save(history);
		return save;
	}
	
//	@Cacheable(cacheNames = "history", key = "#id")
	public List<OrderHistory> getAllHistory(long id){
		List<OrderHistory> list = historyRepo.getAllHistory(id);		
		return list;
	}
	
	public List<Seat> getAllSeat(LocalDate date, String time){
		List<Seat> list = seatRepo.getAllByDate(date, time);
		return list;
	}
	
	public void delete(long id) {
		seatRepo.deleteById(id);
	}
	
	public Customer updateDetail(Customer customer) {
		return customerRepo.save(customer);
	}
	
	public List<MovieDetails> getAllMovie(){
		List<MovieDetails> list = this.movieRepo.findAll();
		return list;
	}
	
	public MovieDetails saveMovie(MovieDetails movies) {
		return movieRepo.save(movies);
	}
	
	public MovieDetails getMovieById(Long id) {
        return movieRepo.findById(id).orElse(null);
    }
	 
	public void deleteMovie(Long id) {
		movieRepo.deleteById(id);
	}
	
	public void saveSales(SalesReport salesReport) {
		salesRepo.save(salesReport);
	}
	
	public List<SalesReport> getAllSales() {
		return salesRepo.findAll();
	}

	public MovieDetails findMovieByName(String movieName) {
		return movieRepo.findByMovieName(movieName);
	}
}
