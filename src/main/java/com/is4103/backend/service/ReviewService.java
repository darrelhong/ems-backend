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
    private BusinessPartnerService bpservice;

    @Autowired
    private BusinessPartnerController bpController;

    public List<Review> getReviewsByEventId(Long id) {
        Event event = eventController.getEventById(id);
        return reviewRepository.findByEvent(event);
    }

    public Review getLatestReviewForEventByBP(Long eid, Long bpId) {
        BusinessPartner bp = bpservice.getBusinessPartnerById(bpId);
        List<Review> reviews = bp.getReviews();
        Review latestReview = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        for (Review r : reviews) {
            if (r.getEvent().getEid() == eid) {
                if (latestReview == null) {
                    latestReview = r;
                } else {
                    LocalDateTime latestReviewDate = LocalDateTime.parse(latestReview.getReviewDateTime(), formatter);
                    LocalDateTime currReviewDate = LocalDateTime.parse(r.getReviewDateTime(), formatter);
                    if (currReviewDate.isAfter(latestReviewDate)) {
                        latestReview = r;
                    }
                }
            }
        }
        return latestReview;
    }

    public List<Review> getReviewsByEO(Long id) {
        // EventOrganiser organiser =
        // eventOrganiserController.getEventOrganiserById(id);
        List<Event> events = eventController.getAllEventsByOrganiser(id);
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            // System.out.println(events.get(i).getReviews() + "events " +
            // events.get(i).getEid());
            if (events.get(i).getReviews() != null) {
                // System.out.println("in reviews" + events.get(i).getReviews());
                List<Review> eventReviews = events.get(i).getReviews();
                for (int h = 0; h < eventReviews.size(); h++) {
                    reviews.add(eventReviews.get(h));
                }
            }
        }
        return reviews;
        // List<Review> filteredReviews = new ArrayList<>();
        // List<Review> reviews = reviewRepository.findAll();
        // for(int i =0; i<reviews.size(); i++){
        // for(int h=0; h<events.size();h++){
        // if(reviews.get(i).getEvent().getEid() == events.get(h).getEid()){
        // filteredReviews.add(reviews.get(i));
        // }
        // }
        // }

        // return filteredReviews;
    }

    @Transactional
    public Review createNewReview(CreateReview reviewRequest) {
        Event event = eventController.getEventById(reviewRequest.getEventId());
        // System.out.println(event + "event");
        Review review = new Review();
        review.setRating(reviewRequest.getRating());
        review.setReviewText(reviewRequest.getReview());
        review.setEvent(event);
        if (reviewRequest.getAttendeeId() != 0L) {
            Attendee attendee = atnController.getAttendeeById(reviewRequest.getAttendeeId());

            review.setAttendee(attendee);
        } else {
            BusinessPartner partner = bpController.getBusinessPartnerById(reviewRequest.getPartnerId());

            review.setPartner(partner);
        }
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String formatDateTime = now.format(formatter);
        review.setReviewDateTime(formatDateTime);
        review = reviewRepository.save(review);

        if (event.getReviews() == null || event.getReviews().isEmpty()) {
            List<Review> eventReviews = new ArrayList<>();
            eventReviews.add(review);
            event.setReviews(eventReviews);
            eoRepository.save(event);
        } else {

            List<Review> eventReviews = new ArrayList<>();
            eventReviews = event.getReviews();
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
