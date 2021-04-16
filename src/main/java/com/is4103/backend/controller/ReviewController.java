package com.is4103.backend.controller;

import java.util.List;

import javax.validation.Valid;

import com.is4103.backend.dto.CreateReview;
import com.is4103.backend.model.Review;
import com.is4103.backend.service.ReviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping(path = "/{id}")
    public List<Review> getReviewsByEventId(@PathVariable Long id) {
        return reviewService.getReviewsByEventId(id);
    }

    @GetMapping(path = "/eo/{id}")
    public List<Review> getReviewsByEO(@PathVariable Long id) {
        return reviewService.getReviewsByEO(id);
    }

    @PostMapping(value = "/create")
    public Review createNewReview(@RequestBody @Valid CreateReview reviewRequest) {
        return reviewService.createNewReview(reviewRequest);
    }

    @GetMapping(path = "/{eid}/{id}")
    public Review getLatestReviewForEventByBP(@PathVariable Long eid, @PathVariable Long id) {
        return reviewService.getLatestReviewForEventByBP(eid, id);
    }
}
