package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest(){
        when(ticketDAO.getNbTickets(anyString())).thenReturn(1);
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void testProcessIncomingVehicle(){
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot((any(ParkingType.class)))).thenReturn(1);
        parkingService.processIncomingVehicle();
        verify(parkingSpotDAO).getNextAvailableSlot(any());
        Ticket ticket;
        lenient().when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true); ;
//        verify(ticketDAO).getNbTickets(anyString());
    }

    @Test
    public void processExitingVehicleTestUnableUpdate(){
        when(ticketDAO.getNbTickets(anyString())).thenReturn(1);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        parkingService.processExitingVehicle();
        verify(ticketDAO).updateTicket(any(Ticket.class));
    }

    @Test
    public void testGetNextParkingNumberIfAvailable(){
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot((any(ParkingType.class)))).thenReturn(1);
        int result = parkingService.getNextParkingNumberIfAvailable().getId();
        assertEquals(1, result);
        verify(parkingSpotDAO).getNextAvailableSlot(any(ParkingType.class));
    }
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound(){
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot((any(ParkingType.class)))).thenReturn(0);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertEquals(null, parkingSpot);
    }
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgum(){
        when(inputReaderUtil.readSelection()).thenReturn(3);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertEquals(null, parkingSpot);
    }
}
