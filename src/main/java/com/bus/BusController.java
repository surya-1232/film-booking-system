package com.bus;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bus.beans.Customer;
import com.bus.beans.MovieDetails;
import com.bus.beans.OrderHistory;
import com.bus.beans.SalesReport;
import com.bus.beans.Seat;
import com.bus.service.CustomerDao;


@Controller
public class BusController {
	
	@Autowired private CustomerDao dao;

	// Opening home page
		@GetMapping("/")
		public String home(Model m, HttpSession session) {
	
			String movie = (String) session.getAttribute("movieName");
			System.out.println(movie + "========Index");
			
			List<MovieDetails> movieList = dao.getAllMovie();
			
			if(movieList == null) {
				movieList = new ArrayList<>();
			}
			m.addAttribute("movieList", movieList);
			m.addAttribute("menu", "dashboard");
	
			return "index";
		}
	
		
	//User Registration form
		@GetMapping("/register")
		public String register(Model m) {
			m.addAttribute("menu", "register");
			return "register";
		}
	
		
	//Registration process
		@PostMapping("/save")
		public String save(@ModelAttribute("customer") Customer customer, BindingResult result) {
			
			if(result.hasErrors()) {
				return "register";
			}
			dao.save(customer);
			return "redirect:/register";
		}

		
	//User Login form
		@GetMapping("/loginForm")
		public String loginForm(@RequestParam(value = "redirect", required = false) String redirect, Model m) {
			m.addAttribute("redirectUrl", redirect != null ? redirect : "/booking-seat");
			m.addAttribute("menu", "login");
			return "login";
		}
		
		
	//Login process
		@PostMapping("/processing")
		public String login(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam(value = "redirect", required = false) String redirect, HttpSession session, Model m) {
			
			//1. Validation
			if(email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
				m.addAttribute("failed", "Email and Password are required.");
				return "login";
			}

			//2. Check DB for Customer and Admin
			Customer customer = dao.login(email, password);
			
			if(customer == null) {
				System.out.println("Login failed for email: " + email);
				m.addAttribute("failed", "Invalid login");
				return "login";
			}
			
			if(customer.getBid() == 1) {
				session.setAttribute("admin", customer);
				System.out.println("Admin session: =====> " + session.getAttribute("admin"));
				return "redirect:/admin/home";
			}
			
			session.setAttribute("user", customer);
			
			System.out.println("Login successful for user: " + customer.getEmail());
			System.out.println("Admin session: ====> " + session.getAttribute("admin"));
			System.out.println("Customer session: ====> " + session.getAttribute("user"));
			
			return "redirect:" + redirect;
		}

	
	//Child Method to check if the movie exists or not. If it is, get it into the seat booking page.
		@GetMapping("/booking")
		public String bookingCheck(@RequestParam("movieName") String movieName, Model m, HttpSession session) {
			
			List<MovieDetails> movies = dao.getAllMovie();
			Customer loggedInUser = (Customer) session.getAttribute("user");
			
			if(movies == null || movies.isEmpty() ) {
				return "redirect:/loginForm";
			}
			
			List<String> checkMovie = new ArrayList<>();
			for (MovieDetails string : movies) {
				checkMovie.add(string.getMovieName());
			}
			if (!checkMovie.contains(movieName)) {
				return "redirect:/loginForm";
			}
				
			if(loggedInUser == null) {
				System.out.println("Is the customer is loggedIn : " + loggedInUser);
				return "redirect:/booking-seat?movieName=" + movieName;
			}
			
			System.out.println(movieName);
		
			session.setAttribute("movieName", movieName);
			return "redirect:/booking-seat?movieName=" + movieName; //booking-seat page
		}
		
		
	//Seat Booking Page.
		@GetMapping("/booking-seat")
		public String getUser(@RequestParam(value="movieName", required=false) String movieName, HttpSession session, Model m) {
			
			Customer customer = (Customer) session.getAttribute("user");
			
			if (customer == null) {
				System.out.println("User not logged in. Redirecting to login for movie: \" + movieName");
				return "redirect:/loginForm";
			}
			
			if("ADMIN".equalsIgnoreCase(customer.getRole())) {
				System.out.println("Admin attempted booking page -> redirecting to admin home.");
				return "redirect:/admin/home";
			}
			
			if(movieName == null || movieName.trim().isEmpty()) {
				System.out.println("No movie selected. Please select movie and come here.");
				return "redirect:/home";
			}
			List<MovieDetails> movie2 = dao.getAllMovie();
			List<String> checkMovie = new ArrayList<>();
			
			for (MovieDetails string : movie2) {
				checkMovie.add(string.getMovieName());
			}
			
			System.out.println(">>> Movie requested by user: " + movieName);
		    System.out.println(">>> Movies in DB:");
		    for (String dbMovie : checkMovie) {
		        System.out.println(" - " + dbMovie);
		    }

			if (!checkMovie.contains(movieName)) {
				System.out.println("Doesn't have the movie. So, it directs to home page.");
				return "redirect:/home";
			}
			System.out.println("Movie Name ===== : " + movieName);
			session.setAttribute("movieName", movieName);

			LocalDate now = LocalDate.now();
			LocalDate monthLimit = LocalDate.now();
			String time = "09:00 am";

			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> seat = customer.getSeat();

			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}
			}

			m.addAttribute("date", now);
			m.addAttribute("time", time);
			m.addAttribute("max", monthLimit.plusMonths(1));
			m.addAttribute("min", monthLimit);
			m.addAttribute("seats", seatNo1);
			m.addAttribute("seat", seat);
			session.setAttribute("user", customer);
			return "dashboard";
		}
			
	//Seat Booking Process
		@PostMapping("/book-seat")
		public String bookSeat(@ModelAttribute("Seat") Seat seat, @RequestParam("movieName") String movieName, HttpSession session, Model m) {
			
			Customer loggedInUser = (Customer) session.getAttribute("user");

			if (loggedInUser == null) {
				return "redirect:/loginForm";
			}
			
			if (seat.getSeatNo() == null || seat.getSeatNo().isEmpty() || movieName == null || movieName.trim().isEmpty()) {
				System.out.println("Seat is null");
				return "redirect:/home";
			}
			
			LocalDate currentDate = LocalDate.now();
			LocalDate bookingDate = (LocalDate) session.getAttribute("bookingdate");
			String bookingTime = (String) session.getAttribute("bookingtime");
			
			if(bookingDate == null) {
				bookingDate = currentDate;
				bookingTime = "09:00 am";
			}
			
			if(bookingDate == null) {
				bookingDate = currentDate;
				bookingTime = "09:00 am";
			}
			
			MovieDetails movie = dao.findMovieByName(movieName);
			if(movie == null) {
				System.out.println("Movie is not found in DB : " + movie);
				return "redirect:/home";
			}
			seat.setMovie(movie);
			
			List<Seat> allBooked = dao.getAllSeat(bookingDate, bookingTime);
			Set<String> bookedNumbers = new HashSet<>();
			for(Seat s : allBooked) {
				bookedNumbers.addAll(s.getSeatNo());
			}
			
			for(String requestedSeat : seat.getSeatNo()) {
				if(bookedNumbers.contains(requestedSeat)) {
					System.out.println("Seat Already booked : " + requestedSeat);
					session.setAttribute("msg", "Some seats are already booked. Please choose others.");
					return "redirect:/booking-seat?movieName=" + movieName;
				}
			}
			
			if ((bookingDate.isBefore(currentDate) || bookingDate.isAfter(currentDate.plusMonths(1)))) {
				System.out.println("Selected date is out of allowed range");
		        return "redirect:/booking-seat?movieName=" + movieName;
		    }
				
			ZoneId defaultZoneId = ZoneId.systemDefault();
			Date operationDate = Date.from(currentDate.atStartOfDay(defaultZoneId).toInstant());
			Date today = Date.from(bookingDate.atStartOfDay(defaultZoneId).toInstant());
					
			List<Double> price = new ArrayList<Double>();
			double total = 0;
			double ticketPrice = 125.22d;
			
			for (String s : seat.getSeatNo()) {
				total = total + ticketPrice;
				price.add(ticketPrice);
			}
			seat.setTotal(total);
			seat.setPrice(price);

			OrderHistory history = new OrderHistory(seat.getSeatNo(), price, total, movieName, today, operationDate, bookingTime, loggedInUser);
			
			dao.saveSeat(seat, loggedInUser, operationDate, bookingTime);
			dao.saveHistory(history, loggedInUser);
			
			session.setAttribute("user", loggedInUser);
			session.setAttribute("msg", "your seat is booked successsfully");
			
			List<String> seatNoList = getAllBookedSeatNumbers();
			m.addAttribute("seats", seatNoList);
			
			return "redirect:/home";
		}

		
	//Helper Method
		private List<String> getAllBookedSeatNumbers() {
			
			List<String> seatNoList = new ArrayList<String>();
			List<Customer> all = dao.getAll();
			for (Customer c : all) {
				for (Seat s : c.getSeat()) {
					for (String s1 : s.getSeatNo()) {
						seatNoList.add(s1);
					}
				}
			}
			return seatNoList;
		}
		
	//User Home Page
		@GetMapping("/home")
		public String mainDashboard(HttpSession session, Model m) {
			session.removeAttribute("bookingdate");
			session.removeAttribute("bookingtime");
			session.removeAttribute("movieName");
			m.addAttribute("menu", "home");

			String message = (String) session.getAttribute("msg");
			
			if(message != null) {
				m.addAttribute("message", message);
				session.removeAttribute("msg");
			}
			
			List<MovieDetails> movieList = dao.getAllMovie();
			
			if(movieList != null) {
				m.addAttribute("listMovie", movieList);
			}
			return "main-dashboard";
		}

		
		
	//User Settings
		@GetMapping("/setting")
		public String getSetting(Model m, HttpSession session) {
			Customer customer = (Customer) session.getAttribute("user");
			m.addAttribute("user", customer);
			m.addAttribute("menu", "setting");
			return "setting";
		}

		
	//User update form
		@GetMapping("/setting/update/{id}")
		public String updateForm(@PathVariable("id") long id, Model m) {
			System.out.println(id + " is updated successfully");
			m.addAttribute("menu", "setting");
			return "update-details";
		}

		
	//update Details
		@PostMapping("/setting/update-details")
		public String updateDetails(@ModelAttribute("customer") Customer cust, HttpSession session) {
			
			String name = cust.getName();
			String email = cust.getEmail();
			Customer customer = (Customer) session.getAttribute("user");
			
			if (customer == null) {
		        return "redirect:/loginForm";
		    }	
			customer.setName(name);
			customer.setEmail(email);
			dao.updateDetail(customer);

			return "redirect:/setting";
		}
	
		
	//About us Page
		@GetMapping("/aboutus")
		public String aboutUsPage() {
			return "aboutus";
		}
		
		
	//Contact us page
		@GetMapping("/contactus")
		public String contactUsPage() {
			return "contactus";
		}
		
		
	//Order history
		@GetMapping("/order-history")
		public String history(HttpSession session, Model m) {
			
			Customer loggedInUser = (Customer) session.getAttribute("user");
			
			if(loggedInUser == null) {
				return "redirect:/loginForm";
			}
			
			Date todayDate = new Date();
			List<OrderHistory> list = dao.getAllHistory(loggedInUser.getBid());
			
			session.setAttribute("user", loggedInUser);
			m.addAttribute("hList", list);
			m.addAttribute("todaydate", todayDate);

			Object dateObj = session.getAttribute("bookingdate");
			
			if(dateObj instanceof LocalDate) {
				System.out.println(dateObj + "is getting.");
				m.addAttribute("menu", "order");
			}
			return "history";
		}

		
	//Admin power to clear All seats
		@GetMapping("/clear-seats")
		public String eraseSeat(HttpSession session) {
		
			Customer loggedInUser = (Customer) session.getAttribute("user");
			
			if(loggedInUser == null) {
				return "redirect:/loginForm";
			}

			if (loggedInUser.getBid() != 1) {
		        return "redirect:/booking-seat";
		    }
			
			LocalDate now = LocalDate.now();
			String time = "09:00 am";
			
			List<Seat> list = dao.getAllSeat(now, time);
			for (Seat seat : list) {
				long id = seat.getsId();
				dao.delete(id);
			}
			return "redirect:/booking-seat";
		}
		
		
		@PostMapping("/check")
		public String checkDate(@RequestParam("localdate") String date, @RequestParam("localtime") String time, Model m, HttpSession session) {
			
			Customer loggedInUser = (Customer) session.getAttribute("user");
			String movie = (String) session.getAttribute("movieName");
			LocalDate monthLimit = LocalDate.now();
			
			if (movie == null) {
				return "home";
			}
			
			LocalDate now = LocalDate.parse(date);
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}
			}

			session.setAttribute("bookingdate", now);
			session.setAttribute("bookingtime", time);
			m.addAttribute("date", now);
			m.addAttribute("max", monthLimit.plusMonths(1));
			m.addAttribute("min", monthLimit);
			m.addAttribute("time", time);
			m.addAttribute("seats", seatNo1);
			
			return (loggedInUser == null) ? "home" : "dashboard";
			
		}
			

	//Admin Home Page
		@GetMapping("/admin/home")
		public String adminHomePage(Model m, HttpSession session) {
			if(session.getAttribute("admin") == null) {
				return "redirect:/loginForm";
			}
			return "admin_home";
		}
		
	//Admin add movies 
		@GetMapping("/admin/add-movie")
		public String showAddMovieForm(HttpSession session, Model model) {
			if (session.getAttribute("admin") == null) {
		        return "redirect:/loginForm";
		    }
			List<MovieDetails> movies = dao.getAllMovies();
			System.out.println("Total movies available now : " + movies.size());
			model.addAttribute("movies", movies);
			return "admin_movies";
		}
	
	//Admin add movies
		@PostMapping("/admin/add-movie")
		public String addMovie(@RequestParam("movieName") String movieName,
								@RequestParam("movieDetails") String movieDetails,
								@RequestParam("image") MultipartFile file,
								HttpSession session) throws IOException {
	
			if (session.getAttribute("admin") == null) {
		        return "redirect:/loginForm";
		    }
			String fileName = file.getOriginalFilename();
			String uploadDir = System.getProperty("user.dir") + "/uploads/images/";
			
			File uploadPath = new File(uploadDir);
			if(!uploadPath.exists()) {
				uploadPath.mkdirs();
			}
			
			file.transferTo(new File(uploadPath, fileName));
			
			MovieDetails movie = new MovieDetails();
			movie.setMovieName(movieName);
			movie.setMovieDetails(movieDetails);
			movie.setImage(fileName);
			
			dao.saveMovie(movie);
			return "redirect:/admin/home";
		}
	
	//Admin show all movie details
		@GetMapping("/admin/movies")
		public String listMovies(HttpSession session, Model m) {
			if(session.getAttribute("admin") == null) {
				return "redirect:/loginForm";
			}
			List<MovieDetails> movies = dao.getAllMovies();
			m.addAttribute("movies", movies);
			return "admin_update_delete_movie";
		}
			
		
	//Admin update the movie
		@GetMapping("/admin/update-movie/{id}")
		public String updateMovie(@PathVariable Long id, HttpSession session, Model m) {
			if(session.getAttribute("admin") == null) {
				return "redirect:/loginForm";
			}
			
			MovieDetails movie = dao.getMovieById(id);
			if(movie == null) {
				return "redirect:/admin/movies";
			}
			System.out.println("==========Movie Name : " +movie);
			m.addAttribute("movie", movie);
			m.addAttribute("updateMode", true);
			System.out.println("================Update Mode : " +movie);
			return "admin_movies";
		}
		
		
		@PostMapping("/admin/update-movie/{id}")
		public String saveUpdatedMovie(@PathVariable Long id, @ModelAttribute MovieDetails movie, @RequestParam(value = "imageFile", required = true) MultipartFile imageFile, HttpSession session) throws IOException {
			if(session.getAttribute("admin") == null) {
				return "redirect:/loginForm";
			}
			
			if(imageFile != null && !imageFile.isEmpty()) {
				String fileName = imageFile.getOriginalFilename();
				movie.setImage(fileName);
			} else {
				MovieDetails existingMovie = dao.getMovieById(id);
				movie.setImage(existingMovie.getImage());
			}
			movie.setMovieId(id);
			dao.saveMovie(movie);
			System.out.println("Movie Name" + movie.getMovieName() + "movie id " + movie.getMovieId());
			return "redirect:/admin/movies";
		}
		
		
	//Admin delete movie
		@PostMapping("/admin/delete-movie/{id}")
		public String deleteMovie(@PathVariable Long id, HttpSession session) {
			if (session.getAttribute("admin") == null) {
		        return "redirect:/loginForm";
		    }
			dao.deleteMovie(id);
			return "redirect:/admin/movies";
		}
		
	
	//Admin show Customer details	
		@GetMapping("/admin/customer-details")
		public String customerDetails(HttpSession session, Model model) {
			if(session.getAttribute("admin") == null) {
				return "redirect:/loginForm";
			}
			
			List<Customer> customers = dao.getAll();
			model.addAttribute("customers", customers);
			return "customer-details";
		}
	
	//Admin shows Movie Sales Report
		@GetMapping("/admin/sales-report")
		public String movieSalesReport(HttpSession session, Model model) {
			if(session.getAttribute("admin") == null) {
				return "redirect:/loginForm";
			}
			List<SalesReport> movies = dao.getAllSales();
			model.addAttribute("sales", movies);
			return "admin-movie-sales-report";
		}
	
	

	

//	Logout process
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		
		session.removeAttribute("user");
		session.removeAttribute("bookingdate");
		session.removeAttribute("bookingtime");
		session.removeAttribute("movieName");

		return "redirect:/";
	}

//	Admin can see all Customers
	@GetMapping("/all-customers-records")
	public String allRecords(Model m, HttpSession session) {
		Customer loggedInUser = (Customer) session.getAttribute("user");
		
		if(loggedInUser == null) {
			return "redirect:/loginForm";
		}
		
		long bid = loggedInUser.getBid();
		if (bid != 1) {
			return "redirect:/booking-seat";
		}
		
		List<Customer> all = dao.getAll();
		m.addAttribute("records", all);
		m.addAttribute("menu", "allusers");
		return "user_records";
	}

//	Admin can see all Customers and their seats
	@GetMapping("/all-seats/{id}")
	public String allSeats(@PathVariable("id") long id, Model m, HttpSession session) {
		Customer loggedInUser = (Customer) session.getAttribute("user");
		
		if(loggedInUser == null) {
			return "redirect:/loginForm";
		}
		
		long bid = loggedInUser.getBid();
		if (bid != 1) {
			return "redirect:/booking-seat";
		}
		List<OrderHistory> list = dao.getAllHistory(id);
		m.addAttribute("seatRecords", list);
		m.addAttribute("menu", "allusers");
		return "seat-records";
	}

//	Exception handling
	@ExceptionHandler(Exception.class)
	public String handleError(Exception ex, Model m, HttpSession session) {
		ex.printStackTrace();
		Customer loggedInUser = (Customer) session.getAttribute("user");
		return (loggedInUser == null) ? "redirect:/loginForm" : "redirect:/home";
	}
	
}
