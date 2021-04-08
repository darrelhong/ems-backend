package com.is4103.backend.dto.event;

import java.time.LocalDateTime;
import java.util.List;

import com.is4103.backend.model.EventStatus;

public interface EventDetailsDto {
    Long getEid();

    String getName();

    String getAddress();

    String getDescriptions();

    Boolean getSellingTicket();

    Float getTicketPrice();

    Boolean getPhysical();

    LocalDateTime getEventStartDate();

    LocalDateTime getEventEndDate();

    LocalDateTime getSaleStartDate();

    LocalDateTime getSalesEndDate();

    List<String> getImages();

    Integer getRating();

    EventStatus getEventStatus();

    Boolean getAvailableForSale();

    List<String> getEventCategory();

    EventOrganiserDetails getEventOrganiser();

    List<SellerProfileSummary> getSellerProfiles();

    interface EventOrganiserDetails {
        String getName();
    }

    interface SellerProfileSummary {
        Long getId();

        String getDescription();

        List<BoothSummary> getBooths();

        List<String> getBrochureImages();
    }

    interface BoothSummary {
        Long getId();

        List<ProductSummary> getProducts();

        Integer getBoothNumber();

        String getDescription();

        BoothSellerProfileSummary getSellerProfile();
    }

    interface BoothSellerProfileSummary {
        BusinessPartnerSummary getBusinessPartner();
    }

    interface BusinessPartnerSummary {
        String getName();
    }

    interface ProductSummary {
        Long getPid();

        String getName();

        String getDescription();

        String getImage();
    }
}
