package com.bus.beans;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "seat")
public class Seat {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "s_id")
	private long sId;
	
	@ElementCollection
	@CollectionTable(name = "seat_seat_no", joinColumns = @JoinColumn(name = "seat_s_id"))
	@Column(name = "seat_no")
	private List<String> seatNo;
	
	@ElementCollection
	@CollectionTable(name = "seat_price", joinColumns = @JoinColumn(name = "seat_s_id"))
	@Column(name = "price")
	private List<Double> price;
	
	@Column(name="total")
	private double total;
	
	@ManyToOne
	@JoinColumn(name = "customer_b_id")
	private Customer customer;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name  = "operation_date_id")
	private CurrentDateOperation operation;
	
	
	@ManyToOne
	@JoinColumn(name = "movie_id", nullable = false)
	private MovieDetails movie;

	public MovieDetails getMovie() {
		return movie;
	}

	public void setMovie(MovieDetails movie) {
		this.movie = movie;
	}

	public long getsId() {
		return sId;
	}

	public void setsId(long sId) {
		this.sId = sId;
	}

	public List<String> getSeatNo() {
		return seatNo;
	}

	public void setSeatNo(List<String> seatNo) {
		this.seatNo = seatNo;
	}

	public List<Double> getPrice() {
		return price;
	}

	public void setPrice(List<Double> price) {
		this.price = price;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CurrentDateOperation getOperation() {
		return operation;
	}

	public void setOperation(CurrentDateOperation operation) {
		this.operation = operation;
	}

	public Seat(List<String> seatNo, List<Double> price, double total, Customer customer,
			CurrentDateOperation operation) {
		super();
		this.seatNo = seatNo;
		this.price = price;
		this.total = total;
		this.customer = customer;
		this.operation = operation;
	}

	public Seat(long sId, List<String> seatNo, List<Double> price, double total, Customer customer,
			CurrentDateOperation operation) {
		super();
		this.sId = sId;
		this.seatNo = seatNo;
		this.price = price;
		this.total = total;
		this.customer = customer;
		this.operation = operation;
	}

	public Seat() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Seat [sId=" + sId + ", seatNo=" + seatNo + ", price=" + price + ", total=" + total + ", customer="
				+ customer + ", operation=" + operation + "]";
	}

		
	
}
