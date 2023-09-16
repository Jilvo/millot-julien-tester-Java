package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount) {
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        int inHour = ticket.getInTime().getHours();
        int outHour = ticket.getOutTime().getHours();
        long compareTime = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        float duration = (float)compareTime/(1000*3600);
        float discount_ratio = 1.0f;
        if(discount) {
            discount_ratio = 0.95f;
        }
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * discount_ratio);

                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * discount_ratio);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
        if ((float)compareTime/(1000*60) <= 30.0){
            ticket.setPrice(0);
        }
    }
}