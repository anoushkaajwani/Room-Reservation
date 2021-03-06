package com.Anoushka.LL.business.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Anoushka.LL.business.domain.RoomReservation;
import com.Anoushka.LL.data.entity.Guest;
import com.Anoushka.LL.data.entity.Reservation;
import com.Anoushka.LL.data.entity.Room;
import com.Anoushka.LL.data.repository.GuestRepository;
import com.Anoushka.LL.data.repository.ReservationRepository;
import com.Anoushka.LL.data.repository.RoomRepository;

@Service
public class ReservationService {
	private final RoomRepository roomRepository;
	private final GuestRepository guestRepository;
	private final ReservationRepository reservationRepository;
	
	@Autowired
	public ReservationService(RoomRepository roomRepository, GuestRepository guestRepository,
			ReservationRepository reservationRepository) {
		super();
		this.roomRepository = roomRepository;
		this.guestRepository = guestRepository;
		this.reservationRepository = reservationRepository;
	}

	public List<RoomReservation> getRoomReservationsForDate(Date date){
		Iterable<Room> rooms = this.roomRepository.findAll();
		
		Map<Long , RoomReservation> roomReservationMap = new HashMap<Long , RoomReservation>();
		rooms.forEach(room -> {
			RoomReservation roomReservation = new RoomReservation();
			roomReservation.setRoomId(room.getRoomId());
			roomReservation.setRoomName(room.getRoomName());
			roomReservation.setRoomNumber(room.getRoomNumber());
			roomReservationMap.put(room.getRoomId(), roomReservation);
			//System.out.println(room);
		});
	
		Iterable<Reservation> reservations = reservationRepository.findReservationByReservationDate(new java.sql.Date(date.getTime())); 
		reservations.forEach(reservation -> {
			RoomReservation roomReservation = roomReservationMap.get(reservation.getRoomId());
			Guest guest = this.guestRepository.findById(reservation.getGuestId()).get();
			roomReservation.setDate(date);
			roomReservation.setFirstName(guest.getFirstName());
			roomReservation.setLastName(guest.getLastName());
			roomReservation.setGuestId(guest.getGuestId());
			});
		
		List<RoomReservation> roomReservations = new ArrayList<RoomReservation>();
		for(Long id: roomReservationMap.keySet()) {
			roomReservations.add(roomReservationMap.get(id));

		}
		//roomReservations.forEach(roomReservation -> System.out.println(roomReservation));
		roomReservations.sort(new Comparator<RoomReservation>() {
			@Override
			public int compare(RoomReservation r1 , RoomReservation r2) {
				if(r1.getRoomName() == r2.getRoomName()) return r1.getRoomNumber().compareTo(r2.getRoomNumber());
				return r1.getRoomName().compareTo(r2.getRoomName());
			}
		});
		return roomReservations;
	}
}
