package com.nganyaexperience.backend.service;

import com.nganyaexperience.backend.entity.Booking;
import com.nganyaexperience.backend.entity.Event;
import com.nganyaexperience.backend.entity.TicketType;
import com.nganyaexperience.backend.repository.BookingRepository;
import com.nganyaexperience.backend.repository.EventRepository;
import com.nganyaexperience.backend.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;

    // ✅ AUTO-GENERATE TICKET CODE
    private String generateTicketCode() {
        String year = String.valueOf(Year.now().getValue());
        String random = UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();

        return "NGX-" + year + "-" + random;
    }

    @Transactional
    public Booking createBooking(
            String name,
            String phone,
            Long eventId,
            Long ticketTypeId
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        TicketType ticket = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // 🚫 SOLD OUT CHECK (UNCHANGED)
        if (ticket.getSold() >= ticket.getCapacity()) {
            throw new RuntimeException("Ticket SOLD OUT");
        }

        // ✅ Increment sold count (UNCHANGED)
        ticket.setSold(ticket.getSold() + 1);
        ticketTypeRepository.save(ticket);

        // ✅ CREATE BOOKING WITH AUTO TICKET CODE
        Booking booking = Booking.builder()
                .customerName(name)
                .phoneNumber(phone)
                .event(event)
                .ticketType(ticket)
                .ticketCode(generateTicketCode())
                .build();

        return bookingRepository.save(booking);
    }
}
