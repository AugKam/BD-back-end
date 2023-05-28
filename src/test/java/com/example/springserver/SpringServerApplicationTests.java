package com.example.springserver;

import com.example.springserver.Constants.Constants;
import com.example.springserver.EncryptDecrypt.EncryptDecrypt;
import com.example.springserver.Enums.*;
import com.example.springserver.controllers.*;
import com.example.springserver.ds.*;
import com.example.springserver.repositories.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
class SpringServerApplicationTests {

	@Mock
	private UserRepository userRepository;
	@Mock
	private CardRepository cardRepository;
	@Mock
	private MoneyTransferRepository moneyTransferRepository;
	@Mock
	private TripRepository tripRepository;
	@Mock
	private TripUsersRepository tripUsersRepository;

	@InjectMocks
	private UserController userController;
	@InjectMocks
	private MoneyTransferController moneyTransferController;
	@InjectMocks
	private TripController tripController;
	@InjectMocks
	private TripUsersController tripUsersController;
	@InjectMocks
	private CardController cardController;

	@Test
	void testSaveUser(){
		User user = new User();
		user.setUserCards(new ArrayList<>());
		user.setEmailApproved(true);
		user.setEmail("email@email.lt");
		user.setPassword("password");
		user.setName("Name");
		user.setBalance(5);
		user.setSurname("Surname");
		user.setId(10);

		when(userRepository.save(user)).thenReturn(user);
		User newUser = userController.saveUser(user);

		assertEquals(true, newUser.getEmailApproved());
		assertEquals(new ArrayList<>(), newUser.getUserCards());
		assertEquals("email@email.lt", newUser.getEmail());
		assertEquals("password", newUser.getPassword());
		assertEquals("Name", newUser.getName());
		assertEquals(5, newUser.getBalance());
		assertEquals("Surname", newUser.getSurname());
		assertEquals(10, newUser.getId());
	}

	@Test
	void testAddCard() {
		User user = new User();
		List<Card> cardList = new ArrayList<>();
		Card card = new Card();
		String cardNumber = "11111111111111";
		String cvc = "111";
		String date = "1111";
		card.setCardNumber(cardNumber);
		card.setCvc(cvc);
		card.setExpireDate(date);
		cardList.add(card);
		user.setUserCards(cardList);

		when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);

		User newUser = userController.addCard(user);

		cardNumber = EncryptDecrypt.encrypt(Constants.KEY,cardNumber);
		cvc = EncryptDecrypt.encrypt(Constants.KEY,cvc);
		date = EncryptDecrypt.encrypt(Constants.KEY,date);

		assertEquals(cardNumber, newUser.getUserCards().get(0).getCardNumber());
		assertEquals(cvc, newUser.getUserCards().get(0).getCvc());
		assertEquals(date, newUser.getUserCards().get(0).getExpireDate());
	}

	@Test
	void testUpdateUserBalance() {
		User user = new User();
		user.setBalance(5);

		when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);

		User newUser = userController.updateUserBalance(user);

		assertEquals(5, newUser.getBalance());
	}

	@Test
	void testLogin() throws Exception {
		User user = new User();
		user.setEmail("email@email.lt");
		user.setPassword("password");
		String request =  "{\"email\":email@email.lt,\"password\":password\"}";

		when(userRepository.findUserByEmailAndPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(user);

		User newUser = userController.login(request);

		assertEquals(user, newUser);
	}

	@Test
	void testGetUserById() {
		User user = new User();
		Card card = new Card();
		card.setCardNumber(EncryptDecrypt.encrypt(Constants.KEY, "11111111111111"));
		card.setCvc(EncryptDecrypt.encrypt(Constants.KEY,"111"));
		card.setExpireDate(EncryptDecrypt.encrypt(Constants.KEY,"1111"));
		card.setCardStatus(CardStatus.ACTIVE);
		List<Card> cardList = new ArrayList<>();
		cardList.add(card);
		user.setUserCards(cardList);
		user.setName("Name");
		user.setEmail("Email");
		user.setId(45);
		when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));

		User newUser = userController.getUserById(user.getId());

		assertEquals(user, newUser);
	}

	@Test
	void TestGetUserCardsByUserId() throws Exception {
		User user = new User();
		Card card = new Card();
		card.setCardNumber(EncryptDecrypt.encrypt(Constants.KEY, "11111111111111"));
		card.setCvc(EncryptDecrypt.encrypt(Constants.KEY,"111"));
		card.setExpireDate(EncryptDecrypt.encrypt(Constants.KEY,"1111"));
		card.setCardStatus(CardStatus.ACTIVE);
		List<Card> cardList = new ArrayList<>();
		cardList.add(card);
		user.setUserCards(cardList);
		user.setName("Name");
		user.setEmail("Email");
		user.setId(45);

		when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));

		List<Card> newCardList = userController.getUserCardsByUserId(user.getId());

		assertEquals(cardList, newCardList);
	}
	@Test
	void Test2GetUserCardsByUserId() throws Exception {
		User user = new User();
		user.setName("Name");
		user.setEmail("Email");
		user.setId(45);

		when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));

		List<Card> newCardList = userController.getUserCardsByUserId(user.getId());
		assertEquals(null, newCardList);
	}

	@Test
	void TestUpdateUserCardStatus() {
		Card card1 = new Card();
		Card card2 = new Card();

		card1.setCardNumber(EncryptDecrypt.encrypt(Constants.KEY, "11111111111111"));
		card1.setCvc(EncryptDecrypt.encrypt(Constants.KEY,"111"));
		card1.setExpireDate(EncryptDecrypt.encrypt(Constants.KEY,"1111"));
		card1.setCardStatus(CardStatus.ACTIVE);

		card2.setCardNumber(EncryptDecrypt.encrypt(Constants.KEY, "11111111111111"));
		card2.setCvc(EncryptDecrypt.encrypt(Constants.KEY,"111"));
		card2.setExpireDate(EncryptDecrypt.encrypt(Constants.KEY,"2512"));
		card2.setCardStatus(CardStatus.ACTIVE);

		List<Card> cardList = new ArrayList<>();
		cardList.add(card1);
		cardList.add(card2);

		when(cardRepository.findAll()).thenReturn(cardList);
		when(cardRepository.saveAll(Mockito.anyList())).thenReturn(cardList);


		List<Card> newCardList = cardController.runEveryMonth();

		assertEquals(cardList, newCardList);
	}

	@Test
	void testCancelTrip() {
		User passenger = new User();
		passenger.setName("Passenger");
		passenger.setBalance(0);

		User driver = new User();
		driver.setName("Passenger");
		driver.setBalance(0);

		Trip trip = new Trip();
		trip.setTripOwner(driver);
		trip.setDeparture("departure");
		trip.setArrival("arrival");
		trip.setTripStatus(TripStatus.CANCELED);
		trip.setPrice(10.0);

		TripUser tripUser = new TripUser();
		tripUser.setTrip(trip);
		tripUser.setPassenger(passenger);
		tripUser.setDriver(driver);
		tripUser.setPassengerStatus(PassengerStatus.AWAITING);

		TripUser tripUser2 = new TripUser();
		tripUser.setTrip(trip);
		tripUser.setDriver(driver);
		tripUser.setPassenger(passenger);
		tripUser.setPassengerStatus(PassengerStatus.CONFIRMED);


		List<TripUser> tripUserList = new ArrayList<>();
		tripUserList.add(tripUser);
		tripUserList.add(tripUser2);

		MoneyTransfer moneyTransfer = new MoneyTransfer();
		moneyTransfer.setTripUser(tripUser);
		moneyTransfer.setPaymentStatus(PaymentStatus.RESERVED);
		moneyTransfer.setFinalPrice(2);
		moneyTransfer.setUsedVirtualMoney(2);


		when(tripUsersRepository.getTripUsersByTrip(Mockito.any())).thenReturn(tripUserList);
		when(moneyTransferRepository.getMoneyTransferByTripUser(Mockito.any())).thenReturn(moneyTransfer);

		Trip canceledTrip = tripController.cancelTrip(trip);

		assertEquals(trip, canceledTrip);
	}

	@Test
	void testSaveTrip(){
		Trip trip = new Trip();
		trip.setTripOwner(new User());
		trip.setDeparture("departure");
		trip.setArrival("arrival");
		trip.setTripStatus(TripStatus.ACTIVE);
		trip.setPrice(10.0);

		User tripOwner = new User();
		tripOwner.setName("Name");

		when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(tripOwner));

		Trip newTrip = tripController.saveTrip(trip);

		assertEquals(trip, newTrip);
	}

	@Test
	void testUpdateTrip(){
		Trip trip = new Trip();
		trip.setTripOwner(new User());
		trip.setDeparture("departure12");
		trip.setArrival("arrival");
		trip.setTripStatus(TripStatus.ACTIVE);
		trip.setPrice(10.0);

		User tripOwner = new User();
		tripOwner.setName("Name");

		when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(tripOwner));

		Trip updatedTrip = tripController.updateTrip(trip);

		assertEquals(trip, updatedTrip);
	}

	@Test
	void TestGetAllTrips(){
		Trip trip1 = new Trip();
		trip1.setDeparture("departure1");
		trip1.setArrival("arrival1");
		trip1.setTripOwner(new User());
		trip1.setPrice(10.1);
		trip1.setId(10);
		trip1.setNumOfSeats(2);

		Trip trip2 = new Trip();
		trip2.setDeparture("departure2");
		trip2.setArrival("arrival2");
		trip2.setTripOwner(new User());
		trip2.setPrice(8.1);
		trip2.setId(11);
		trip2.setNumOfSeats(4);

		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip1);
		tripList.add(trip2);

		when(tripRepository.findAll()).thenReturn(tripList);

		List<Trip> newTripList = tripController.getAllTrips();

		assertEquals(tripList, newTripList);
	}

	@Test
	void testFilterTrips(){
		Trip trip1 = new Trip();
		trip1.setDeparture("departure1");
		trip1.setArrival("arrival1");
		trip1.setTripOwner(new User());
		trip1.setPrice(10.1);
		trip1.setId(10);
		trip1.setNumOfSeats(0);

		Trip trip2 = new Trip();
		trip2.setDeparture("departure2");
		trip2.setArrival("arrival2");
		trip2.setTripOwner(new User());
		trip2.setPrice(8.1);
		trip2.setId(11);
		trip2.setNumOfSeats(4);

		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip1);
		tripList.add(trip2);

		when(tripRepository.findTripsByDepartureContainingAndArrivalContainingAndTripStatus(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(tripList);

		List<Trip> newTripList = tripController.filterTrips(trip1);

		tripList = new ArrayList<>();
		tripList.add(trip2);

		assertEquals(tripList, newTripList);
	}

	@Test
	void testFilterTripsByDeparture(){
		Trip trip1 = new Trip();
		trip1.setDeparture("departure1");
		trip1.setArrival("arrival1");
		trip1.setTripOwner(new User());
		trip1.setPrice(10.1);
		trip1.setId(10);
		trip1.setNumOfSeats(0);

		Trip trip2 = new Trip();
		trip2.setDeparture("departure2");
		trip2.setArrival("arrival2");
		trip2.setTripOwner(new User());
		trip2.setPrice(8.1);
		trip2.setId(11);
		trip2.setNumOfSeats(4);

		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip1);
		tripList.add(trip2);

		when(tripRepository.findTripsByDepartureContainingAndTripStatus(Mockito.anyString(), Mockito.any())).thenReturn(tripList);

		List<Trip> newTripList = tripController.filterTripsByDeparture(trip1);

		tripList = new ArrayList<>();
		tripList.add(trip2);

		assertEquals(tripList, newTripList);
	}

	@Test
	void testFilterTripsByArrival(){
		Trip trip1 = new Trip();
		trip1.setDeparture("departure1");
		trip1.setArrival("arrival1");
		trip1.setTripOwner(new User());
		trip1.setPrice(10.1);
		trip1.setId(10);
		trip1.setNumOfSeats(0);

		Trip trip2 = new Trip();
		trip2.setDeparture("departure2");
		trip2.setArrival("arrival2");
		trip2.setTripOwner(new User());
		trip2.setPrice(8.1);
		trip2.setId(11);
		trip2.setNumOfSeats(4);

		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip1);
		tripList.add(trip2);

		when(tripRepository.findTripsByArrivalContainingAndTripStatus(Mockito.anyString(), Mockito.any())).thenReturn(tripList);

		List<Trip> newTripList = tripController.filterTripsByArrival(trip1);

		tripList = new ArrayList<>();
		tripList.add(trip2);

		assertEquals(tripList, newTripList);
	}

	@Test
	void testGetAllActiveTrips(){
		Trip trip1 = new Trip();
		trip1.setDeparture("departure1");
		trip1.setArrival("arrival1");
		trip1.setTripOwner(new User());
		trip1.setPrice(10.1);
		trip1.setTripStatus(TripStatus.ACTIVE);
		trip1.setId(10);
		trip1.setNumOfSeats(0);

		Trip trip2 = new Trip();
		trip2.setDeparture("departure2");
		trip2.setArrival("arrival2");
		trip2.setTripOwner(new User());
		trip2.setPrice(8.1);
		trip2.setTripStatus(TripStatus.ACTIVE);
		trip2.setId(11);
		trip2.setNumOfSeats(4);

		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip1);
		tripList.add(trip2);

		when(tripRepository.findTripsByTripStatus(Mockito.any())).thenReturn(tripList);

		List<Trip> newTripList = tripController.getAllActiveTrips(trip1);

		tripList = new ArrayList<>();
		tripList.add(trip2);

		assertEquals(tripList, newTripList);
	}

	@Test
	void test1GetTripById(){
		Trip trip = new Trip();
		trip.setDeparture("departure1");
		trip.setArrival("arrival1");
		trip.setTripOwner(new User());
		trip.setPrice(10.1);
		trip.setTripStatus(TripStatus.ACTIVE);
		trip.setId(10);
		trip.setNumOfSeats(0);
		trip.setId(12);

		when(tripRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(trip));

		Trip newTrip = tripController.getTripById(trip.getId());

		assertEquals(trip, newTrip);
	}


	@Test
	void test2GetTripById(){
		Trip trip = new Trip();
		trip.setDeparture("departure1");
		trip.setArrival("arrival1");
		trip.setTripOwner(new User());
		trip.setPrice(10.1);
		trip.setTripStatus(TripStatus.ACTIVE);
		trip.setId(100);
		trip.setNumOfSeats(0);
		trip.setId(120);

		Trip newTrip = tripController.getTripById(trip.getId());

		assertEquals(null, newTrip);

	}
	@Test
	void testGetActiveTripBuTripOwner() {
		Trip trip1 = new Trip();
		trip1.setDeparture("departure1");
		trip1.setArrival("arrival1");
		trip1.setTripOwner(new User());
		trip1.setPrice(10.1);
		trip1.setTripStatus(TripStatus.ACTIVE);
		trip1.setId(10);
		trip1.setNumOfSeats(0);

		Trip trip2 = new Trip();
		trip2.setDeparture("departure2");
		trip2.setArrival("arrival2");
		trip2.setTripOwner(new User());
		trip2.setPrice(8.1);
		trip2.setTripStatus(TripStatus.CANCELED);
		trip2.setId(11);
		trip2.setNumOfSeats(4);

		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip1);
		tripList.add(trip2);

		when(tripRepository.getTripsByTripOwner(Mockito.any())).thenReturn(tripList);

		List<Trip> activeTrips = tripController.getActiveTripByTripOwner(new User());

		tripList = new ArrayList<>();
		tripList.add(trip1);

		assertEquals(tripList, activeTrips);
	}

	@Test
	void testGetInactiveTripBuTripOwner() {
		Trip trip1 = new Trip();
		trip1.setDeparture("departure1");
		trip1.setArrival("arrival1");
		trip1.setTripOwner(new User());
		trip1.setPrice(10.1);
		trip1.setTripStatus(TripStatus.ACTIVE);
		trip1.setId(10);
		trip1.setNumOfSeats(0);

		Trip trip2 = new Trip();
		trip2.setDeparture("departure2");
		trip2.setArrival("arrival2");
		trip2.setTripOwner(new User());
		trip2.setPrice(8.1);
		trip2.setTripStatus(TripStatus.CANCELED);
		trip2.setId(11);
		trip2.setNumOfSeats(4);

		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip1);
		tripList.add(trip2);

		when(tripRepository.getTripsByTripOwner(Mockito.any())).thenReturn(tripList);

		List<Trip> activeTrips = tripController.getInactiveTripByTripOwner(new User());

		tripList = new ArrayList<>();
		tripList.add(trip2);

		assertEquals(tripList, activeTrips);
	}

	@Test
	void testUpdateTripStatus(){
		User passenger = new User();
		passenger.setBalance(10);
		passenger.setName("Name");

		Trip trip1 = new Trip();
		trip1.setDeparture("departure1");
		trip1.setArrival("arrival1");
		trip1.setTripOwner(new User());
		trip1.setPrice(10.1);
		trip1.setDepartureDate(LocalDateTime.of(2022,1,1,1,1,1,1));
		trip1.setTripStatus(TripStatus.ACTIVE);
		trip1.setId(10);
		trip1.setNumOfSeats(0);

		Trip trip2 = new Trip();
		trip2.setDeparture("departure2");
		trip2.setArrival("arrival2");
		trip2.setTripOwner(new User());
		trip2.setPrice(8.1);
		trip2.setTripStatus(TripStatus.CANCELED);
		trip2.setDepartureDate(LocalDateTime.of(2024,1,1,1,1,1,1));
		trip2.setId(11);
		trip2.setNumOfSeats(4);
		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip1);
		tripList.add(trip2);

		List<TripUser> tripUserList = new ArrayList<>();
		TripUser tripUser1 = new TripUser();
		tripUser1.setTrip(trip1);
		tripUser1.setPassengerStatus(PassengerStatus.AWAITING);
		tripUserList.add(tripUser1);
		tripUser1.setPassenger(passenger);

		MoneyTransfer moneyTransfer = new MoneyTransfer();
		moneyTransfer.setTripUser(tripUser1);
		moneyTransfer.setPaymentStatus(PaymentStatus.RESERVED);
		moneyTransfer.setUsedVirtualMoney(2);
		moneyTransfer.setPaymentMethod(PaymentMethod.CARD);

		when(tripRepository.findAll()).thenReturn(tripList);
		when(tripRepository.saveAll(Mockito.anyList())).thenReturn(tripList);
		when(tripUsersRepository.getTripUsersByTrip(Mockito.any())).thenReturn(tripUserList);
		when(moneyTransferRepository.getMoneyTransferByTripUser(Mockito.any())).thenReturn(moneyTransfer);

		List<Trip> updatedTripList = tripController.runEveryMinute();

		tripList.get(0).setTripStatus(TripStatus.CANCELED);

		assertEquals(tripList, updatedTripList);
	}

	@Test
	void test1FinishTripsWithoutPassengers(){
		Trip trip = new Trip();
		trip.setDeparture("departure2");
		trip.setArrival("arrival2");
		trip.setTripOwner(new User());
		trip.setPrice(8.1);
		trip.setTripStatus(TripStatus.ONGOING);
		trip.setDepartureDate(LocalDateTime.of(2024,1,1,1,1,1,1));
		trip.setId(11);
		trip.setNumOfSeats(4);
		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip);

		User passenger = new User();
		passenger.setBalance(10);
		passenger.setName("Name");

		List<TripUser> tripUserList = new ArrayList<>();
		TripUser tripUser1 = new TripUser();
		tripUser1.setTrip(trip);
		tripUser1.setPassengerStatus(PassengerStatus.AWAITING);
		tripUser1.setPassenger(passenger);
		tripUserList.add(tripUser1);

		List<Trip> updatedTripList = tripController.finishTripsWithoutPassengers(tripList);

		tripList.get(0).setTripStatus(TripStatus.CANCELED);

		assertEquals(tripList, updatedTripList);
	}

	@Test
	void test2FinishTripsWithoutPassengers(){
		Trip trip = new Trip();
		trip.setDeparture("departure2");
		trip.setArrival("arrival2");
		trip.setTripOwner(new User());
		trip.setPrice(8.1);
		trip.setTripStatus(TripStatus.ONGOING);
		trip.setDepartureDate(LocalDateTime.of(2024,1,1,1,1,1,1));
		trip.setId(11);
		trip.setNumOfSeats(4);
		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip);

		User passenger = new User();
		passenger.setBalance(10);
		passenger.setName("Name");

		List<TripUser> tripUserList = new ArrayList<>();
		TripUser tripUser1 = new TripUser();
		tripUser1.setTrip(trip);
		tripUser1.setPassengerStatus(PassengerStatus.CONFIRMED);
		tripUser1.setPassenger(passenger);
		tripUserList.add(tripUser1);

		when(tripUsersRepository.getTripUsersByTrip(Mockito.any())).thenReturn(tripUserList);
		List<Trip> updatedTripList = tripController.finishTripsWithoutPassengers(tripList);

		assertEquals(tripList,updatedTripList);

	}
	@Test
	void test3FinishTripsWithoutPassengers(){

		Trip trip = new Trip();
		trip.setDeparture("departure2");
		trip.setArrival("arrival2");
		trip.setTripOwner(new User());
		trip.setPrice(8.1);
		trip.setTripStatus(TripStatus.ONGOING);
		trip.setDepartureDate(LocalDateTime.of(2024,1,1,1,1,1,1));
		trip.setId(11);
		trip.setNumOfSeats(4);
		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip);

		User passenger = new User();
		passenger.setBalance(10);
		passenger.setName("Name");

		List<TripUser> tripUserList = new ArrayList<>();
		TripUser tripUser1 = new TripUser();
		tripUser1.setTrip(trip);
		tripUser1.setPassengerStatus(PassengerStatus.REJECTED);
		tripUser1.setPassenger(passenger);
		tripUserList.add(tripUser1);

		when(tripUsersRepository.getTripUsersByTrip(Mockito.any())).thenReturn(tripUserList);
		List<Trip> updatedTripList = tripController.finishTripsWithoutPassengers(tripList);

		tripList.get(0).setTripStatus(TripStatus.CANCELED);
		assertEquals(tripList,updatedTripList);
	}

	@Test
	void testGetMoneyTransferByUser() {

		Trip trip = new Trip();
		trip.setDeparture("departure2");
		trip.setArrival("arrival2");
		trip.setTripOwner(new User());
		trip.setPrice(8.1);
		trip.setTripStatus(TripStatus.ONGOING);
		trip.setDepartureDate(LocalDateTime.of(2024,1,1,1,1,1,1));
		trip.setId(11);
		trip.setNumOfSeats(4);
		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip);

		User passenger = new User();
		passenger.setBalance(10);
		passenger.setName("Name");

		List<TripUser> tripUserList = new ArrayList<>();
		TripUser tripUser1 = new TripUser();
		tripUser1.setTrip(trip);
		tripUser1.setPassengerStatus(PassengerStatus.REJECTED);
		tripUser1.setPassenger(passenger);
		tripUserList.add(tripUser1);

		MoneyTransfer moneyTransfer = new MoneyTransfer();
		moneyTransfer.setTripUser(tripUser1);
		moneyTransfer.setPaymentStatus(PaymentStatus.RESERVED);
		List<MoneyTransfer> moneyTransferList = new ArrayList<>();
		moneyTransferList.add(moneyTransfer);

		when(tripUsersRepository.getTripUsersByPassengerOrDriver(Mockito.any(), Mockito.any())).thenReturn(tripUserList);
		when(moneyTransferRepository.getMoneyTransferByTripUser(Mockito.any())).thenReturn(moneyTransfer);

		List<MoneyTransfer> newMoneyTransferList = moneyTransferController.getMoneyTransferByUser(passenger);

		assertEquals(moneyTransferList, newMoneyTransferList);
	}

	@Test
	void test1FinishTripForPassenger() {
		Trip trip = new Trip();
		trip.setDeparture("departure2");
		trip.setArrival("arrival2");
		trip.setTripOwner(new User());
		trip.setPrice(8.1);
		trip.setTripStatus(TripStatus.ONGOING);
		trip.setDepartureDate(LocalDateTime.of(2024,1,1,1,1,1,1));
		trip.setId(11);
		trip.setNumOfSeats(4);
		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip);

		User passenger = new User();
		passenger.setBalance(10);
		passenger.setName("Name");

		User driver = new User();
		driver.setBalance(0);
		driver.setName("Name1");

		TripUser tripUser = new TripUser();
		tripUser.setTrip(trip);
		tripUser.setPassengerStatus(PassengerStatus.DELIVERED);
		tripUser.setPassenger(passenger);
		tripUser.setDriver(driver);

		MoneyTransfer moneyTransfer = new MoneyTransfer();
		moneyTransfer.setTripUser(tripUser);
		moneyTransfer.setPaymentMethod(PaymentMethod.CARD);
		moneyTransfer.setPaymentStatus(PaymentStatus.RESERVED);

		when(moneyTransferRepository.getMoneyTransferByTripUser(Mockito.any())).thenReturn(moneyTransfer);
		when(tripUsersRepository.save(Mockito.any())).thenReturn(tripUser);

		MoneyTransfer newMoneyTransfer = moneyTransferController.finishTripForPassenger(moneyTransfer);
		moneyTransfer.setPaymentStatus(PaymentStatus.COMPLETED);
		assertEquals(moneyTransfer, newMoneyTransfer);

	}

	@Test
	void test2FinishTripForPassenger() {
		Trip trip = new Trip();
		trip.setDeparture("departure2");
		trip.setArrival("arrival2");
		trip.setTripOwner(new User());
		trip.setPrice(8.1);
		trip.setTripStatus(TripStatus.ONGOING);
		trip.setDepartureDate(LocalDateTime.of(2024,1,1,1,1,1,1));
		trip.setId(11);
		trip.setNumOfSeats(4);
		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip);

		User passenger = new User();
		passenger.setBalance(10);
		passenger.setName("Name");

		User driver = new User();
		driver.setBalance(0);
		driver.setName("Name1");

		TripUser tripUser = new TripUser();
		tripUser.setTrip(trip);
		tripUser.setPassengerStatus(PassengerStatus.CONFIRMED);
		tripUser.setPassenger(passenger);
		tripUser.setDriver(driver);

		MoneyTransfer moneyTransfer = new MoneyTransfer();
		moneyTransfer.setTripUser(tripUser);
		moneyTransfer.setPaymentMethod(PaymentMethod.CASH);
		moneyTransfer.setPaymentStatus(PaymentStatus.RESERVED);

		TripUser tripUser2 = new TripUser();
		tripUser2.setTrip(trip);
		tripUser2.setPassenger(passenger);
		tripUser2.setDriver(driver);
		tripUser2.setPassengerStatus(PassengerStatus.DELIVERED);
		List <TripUser> tripUserList = new ArrayList<>();
		tripUserList.add(tripUser2);

		when(moneyTransferRepository.getMoneyTransferByTripUser(Mockito.any())).thenReturn(moneyTransfer);
		when(tripUsersRepository.save(Mockito.any())).thenReturn(tripUser);
		when(tripUsersRepository.getTripUsersByTrip(Mockito.any())).thenReturn(tripUserList);

		MoneyTransfer newMoneyTransfer = moneyTransferController.finishTripForPassenger(moneyTransfer);
		moneyTransfer.setPaymentStatus(PaymentStatus.COMPLETED);
		assertEquals(moneyTransfer, newMoneyTransfer);
	}

	@Test
	void test1CreateMoneyTransfer() {
		Trip trip = new Trip();
		trip.setDeparture("departure2");
		trip.setArrival("arrival2");
		trip.setTripOwner(new User());
		trip.setPrice(8.1);
		trip.setTripStatus(TripStatus.ONGOING);
		trip.setDepartureDate(LocalDateTime.of(2024,1,1,1,1,1,1));
		trip.setId(11);
		trip.setNumOfSeats(4);
		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip);

		User passenger = new User();
		passenger.setBalance(10);
		passenger.setName("Name");

		User driver = new User();
		driver.setBalance(0);
		driver.setName("Name1");

		TripUser tripUser = new TripUser();
		tripUser.setTrip(trip);
		tripUser.setPassengerStatus(PassengerStatus.CONFIRMED);
		tripUser.setPassenger(passenger);
		tripUser.setDriver(driver);

		MoneyTransfer moneyTransfer = new MoneyTransfer();
		moneyTransfer.setTripUser(tripUser);
		moneyTransfer.setPaymentMethod(PaymentMethod.CASH);

		MoneyTransfer newMoneyTransfer = moneyTransferController.createMoneyTransfer(moneyTransfer);

		moneyTransfer.setPaymentStatus(PaymentStatus.RESERVED);
		assertEquals(moneyTransfer, newMoneyTransfer);
	}

	@Test
	void test2CreateMoneyTransfer() {
		Trip trip = new Trip();
		trip.setDeparture("departure2");
		trip.setArrival("arrival2");
		trip.setTripOwner(new User());
		trip.setPrice(20.0);
		trip.setTripStatus(TripStatus.ONGOING);
		trip.setDepartureDate(LocalDateTime.of(2024,1,1,1,1,1,1));
		trip.setId(11);
		trip.setNumOfSeats(4);
		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip);

		User passenger = new User();
		passenger.setBalance(10);
		passenger.setName("Name");

		User driver = new User();
		driver.setBalance(0);
		driver.setName("Name1");

		TripUser tripUser = new TripUser();
		tripUser.setTrip(trip);
		tripUser.setPassengerStatus(PassengerStatus.CONFIRMED);
		tripUser.setPassenger(passenger);
		tripUser.setDriver(driver);

		MoneyTransfer moneyTransfer = new MoneyTransfer();
		moneyTransfer.setTripUser(tripUser);
		moneyTransfer.setPaymentMethod(PaymentMethod.CASH);

		MoneyTransfer newMoneyTransfer = moneyTransferController.createMoneyTransfer(moneyTransfer);

		moneyTransfer.setPaymentStatus(PaymentStatus.RESERVED);
		assertEquals(moneyTransfer, newMoneyTransfer);
	}

	@Test
	void testGetPaymentMethod() {
		Trip trip = new Trip();
		trip.setDeparture("departure2");
		trip.setArrival("arrival2");
		trip.setTripOwner(new User());
		trip.setPrice(20.0);
		trip.setTripStatus(TripStatus.ONGOING);
		trip.setDepartureDate(LocalDateTime.of(2024,1,1,1,1,1,1));
		trip.setId(11);
		trip.setNumOfSeats(4);
		List<Trip> tripList = new ArrayList<>();
		tripList.add(trip);

		User passenger = new User();
		passenger.setBalance(10);
		passenger.setName("Name");

		User driver = new User();
		driver.setBalance(0);
		driver.setName("Name1");

		TripUser tripUser = new TripUser();
		tripUser.setTrip(trip);
		tripUser.setPassengerStatus(PassengerStatus.CONFIRMED);
		tripUser.setPassenger(passenger);
		tripUser.setDriver(driver);

		MoneyTransfer moneyTransfer = new MoneyTransfer();
		moneyTransfer.setTripUser(tripUser);
		moneyTransfer.setPaymentMethod(PaymentMethod.CASH);

		when(moneyTransferRepository.getMoneyTransferByTripUser(Mockito.any())).thenReturn(moneyTransfer);

		MoneyTransfer newMoneyTransfer = moneyTransferController.getPaymentMethod(tripUser);

		assertEquals(moneyTransfer, newMoneyTransfer);
	}

	@Test
	void testSaveTripUser() {
		TripUser tripUser = new TripUser();

		tripUser.setPassengerStatus(PassengerStatus.CANCELED);
		tripUser.setTrip(new Trip());
		tripUser.setPassenger(new User());
		tripUser.setDriver(new User());

		TripUser newTripUser = tripUsersController.save(tripUser);

		assertEquals(tripUser, newTripUser);
	}

	@Test
	void testCreateReservation() {
		Trip trip = new Trip();
		trip.setDeparture("departure");
		trip.setArrival("arrival");

		User user = new User();
		user.setName("Name");

		trip.setTripOwner(user);
		TripUser tripUser = new TripUser();
		tripUser.setTrip(trip);
		tripUser.setDriver(user);
		tripUser.setPassenger(user);

		when(tripRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(trip));
		when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
		TripUser newTripUser = tripUsersController.createReservation(tripUser);

		tripUser.setPassengerStatus(PassengerStatus.AWAITING);

		assertEquals(tripUser.getTrip(), newTripUser.getTrip());
		assertEquals(tripUser.getPassenger(), newTripUser.getPassenger());
		assertEquals(tripUser.getDriver(), newTripUser.getDriver());
	}

	@Test
	void testGetActiveTripsByPassenger(){
		User user = new User();
		user.setName("Name");
		user.setSurname("Surname");

		Trip trip1 = new Trip();
		trip1.setArrival("arrival1");
		trip1.setDeparture("departure1");

		Trip trip2 = new Trip();
		trip2.setArrival("arrival2");
		trip2.setDeparture("departure2");

		TripUser tripUser1 = new TripUser();
		tripUser1.setTrip(trip1);
		tripUser1.setPassenger(user);
		tripUser1.setPassengerStatus(PassengerStatus.AWAITING);

		TripUser tripUser2 = new TripUser();
		tripUser2.setTrip(trip2);
		tripUser2.setPassenger(user);
		tripUser2.setPassengerStatus(PassengerStatus.DELIVERED);

		TripUser tripUser3 = new TripUser();
		tripUser3.setTrip(trip2);
		tripUser3.setPassenger(user);
		tripUser3.setPassengerStatus(PassengerStatus.REJECTED);

		List<TripUser> tripUserList = new ArrayList<>();
		tripUserList.add(tripUser1);
		tripUserList.add(tripUser2);
		tripUserList.add(tripUser3);

		when(tripUsersRepository.getTripUsersByPassenger(Mockito.any())).thenReturn(tripUserList);

		List<TripUser> newTripUserList = tripUsersController.getActiveTripsByPassenger(user);

		tripUserList.remove(2);

		assertEquals(tripUserList, newTripUserList);
	}

	@Test
	void testGetInactiveTripsByPassenger(){
		User user = new User();
		user.setName("Name");
		user.setSurname("Surname");

		Trip trip1 = new Trip();
		trip1.setArrival("arrival1");
		trip1.setDeparture("departure1");

		Trip trip2 = new Trip();
		trip2.setArrival("arrival2");
		trip2.setDeparture("departure2");

		TripUser tripUser1 = new TripUser();
		tripUser1.setTrip(trip1);
		tripUser1.setPassenger(user);
		tripUser1.setPassengerStatus(PassengerStatus.AWAITING);

		TripUser tripUser2 = new TripUser();
		tripUser2.setTrip(trip2);
		tripUser2.setPassenger(user);
		tripUser2.setPassengerStatus(PassengerStatus.DELIVERED);

		TripUser tripUser3 = new TripUser();
		tripUser3.setTrip(trip2);
		tripUser3.setPassenger(user);
		tripUser3.setPassengerStatus(PassengerStatus.REJECTED);

		List<TripUser> tripUserList = new ArrayList<>();
		tripUserList.add(tripUser1);
		tripUserList.add(tripUser2);
		tripUserList.add(tripUser3);

		when(tripUsersRepository.getTripUsersByPassenger(Mockito.any())).thenReturn(tripUserList);

		List<TripUser> newTripUserList = tripUsersController.getInactiveTripsByPassenger(user);

		tripUserList.remove(0);
		tripUserList.remove(0);

		assertEquals(tripUserList, newTripUserList);
	}
	@Test
	void testGetTripUserByTrip() {
		User user = new User();
		user.setName("Name");
		user.setSurname("Surname");
		Trip trip2 = new Trip();
		trip2.setArrival("arrival2");
		trip2.setDeparture("departure2");

		TripUser tripUser2 = new TripUser();
		tripUser2.setTrip(trip2);
		tripUser2.setPassenger(user);
		tripUser2.setPassengerStatus(PassengerStatus.DELIVERED);

		TripUser tripUser3 = new TripUser();
		tripUser3.setTrip(trip2);
		tripUser3.setPassenger(user);
		tripUser3.setPassengerStatus(PassengerStatus.REJECTED);

		List<TripUser> tripUserList = new ArrayList<>();
		tripUserList.add(tripUser2);
		tripUserList.add(tripUser3);
		when(tripUsersRepository.getTripUsersByTrip(Mockito.any())).thenReturn(tripUserList);
		List<TripUser> newTripUserList = tripUsersController.getTripUsersByTrip(trip2);
		assertEquals(tripUserList, newTripUserList);
	}
}
