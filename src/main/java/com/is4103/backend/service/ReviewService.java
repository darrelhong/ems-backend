package com.is4103.backend.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.is4103.backend.controller.AttendeeController;
import com.is4103.backend.controller.BusinessPartnerController;
import com.is4103.backend.controller.EventController;
import com.is4103.backend.controller.EventOrganiserController;
import com.is4103.backend.dto.CreateReview;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.Review;
import com.is4103.backend.repository.AttendeeRepository;
import com.is4103.backend.repository.BusinessPartnerRepository;
import com.is4103.backend.repository.EventRepository;
import com.is4103.backend.repository.ReviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EventRepository eoRepository;

    @Autowired
    private AttendeeRepository atnRepository;

    @Autowired
    private BusinessPartnerRepository bpRepository;

    @Autowired
    private EventController eventController;

    @Autowired
    private EventOrganiserController eventOrganiserController;

    @Autowired
    private AttendeeController atnController;

    @Autowired
    private BusinessPartnerController bpController;

    public List<Review> getReviewsByEventId(Long id) {
        Event event = eventController.getEventById(id);
        return reviewRepository.findByEvent(event);
        

    }

    public List<Review> getReviewsByEO(Long id){
        // EventOrganiser organiser = eventOrganiserController.getEventOrganiserById(id);
        List<Event> events = eventController.getAllEventsByOrganiser(id);
        List<Review> reviews = new ArrayList<>();
        for(int i=0; i<events.size(); i++){
            List<Review> eventReviews = events.get(i).getReviews();
            for(int h=0; h<eventReviews.size();h++){
                reviews.add(eventReviews.get(h));
            }
        }
        return reviews;
    }

    @Transactional
    public Review createNewReview(CreateReview reviewRequest) {
        Event event = eventController.getEventById(reviewRequest.getEventId());

        Review review = new Review();
        review.setRating(reviewRequest.getRating());
        review.setReviewText(reviewRequest.getReview());
        review.setEvent(event);
        LocalDateTime now = LocalDateTime.now();

        System.out.println("Before : " + now);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String formatDateTime = now.format(formatter);
        review.setReviewDateTime(formatDateTime);

        System.out.println("After : " + formatDateTime);
        if (reviewRequest.getAttendeeId() != 0L) {
            Attendee attendee = atnController.getAttendeeById(reviewRequest.getAttendeeId());
            System.out.println("is attendee" + attendee);

            review.setAttendee(attendee);
        } else {
            BusinessPartner partner = bpController.getBusinessPartnerById(reviewRequest.getPartnerId());
            System.out.println("is partner" + partner);

            review.setPartner(partner);
        }
        review = reviewRepository.save(review);

        if (event.getReviews() == null || event.getReviews().isEmpty()) {

            List<Review> eventReviews = new ArrayList<>();
            eventReviews.add(review);
            event.setReviews(eventReviews);

            eoRepository.save(event);

        } else {

            List<Review> eventReviews = event.getReviews();
            eventReviews.add(review);
            event.setReviews(eventReviews);
            eoRepository.save(event);

        }

        if (reviewRequest.getAttendeeId() != 0L) {
            Attendee attendee = atnController.getAttendeeById(reviewRequest.getAttendeeId());
            if (attendee.getReviews() == null) {
                List<Review> atnReviews = new ArrayList<>();
                atnReviews.add(review);
                attendee.setReviews(atnReviews);
                atnRepository.save(attendee);
            } else {

                List<Review> atnReviews = attendee.getReviews();
                atnReviews.add(review);
                attendee.setReviews(atnReviews);
                atnRepository.save(attendee);

            }

        } else {
            BusinessPartner partner = bpController.getBusinessPartnerById(reviewRequest.getPartnerId());
            if (partner.getReviews() == null) {
                List<Review> bpReviews = new ArrayList<>();
                bpReviews.add(review);
                partner.setReviews(bpReviews);
                System.out.println(bpReviews);

                bpRepository.save(partner);
            } else {
                List<Review> bpReviews = partner.getReviews();
                bpReviews.add(review);
                partner.setReviews(bpReviews);
                bpRepository.save(partner);
            }

        }

        return review;
    }

}
