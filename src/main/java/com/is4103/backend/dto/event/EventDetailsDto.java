package com.is4103.backend.dto.event;

import java.time.LocalDateTime;
import java.util.List;

import com.is4103.backend.model.EventStatus;

public interface EventDetailsDto {
    Long getEid();

    Boolean getIsVip();

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

    List<String> getCategory();

    EventOrganiserDetails getEventOrganiser();

    List<SellerProfileSummary> getSellerProfiles();

    Integer getBoothCapacity();

    Float getBoothPrice();

    String getBoothLayout();

    interface EventOrganiserDetails {
        String getName();

        String getId();
    }

    interface SellerProfileSummary {
        Long getId();

        String getDescription();

        List<BoothSummary> getBooths();

        List<String> getBrochureImages();

        BusinessPartnerSummary getBusinessPartner();
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

        Long getId();
    }

    interface BusinessPartnerSummary {
        String getName();

        String getId();
    }

    interface ProductSummary {
        Long getPid();

        String getName();

        String getDescription();

        String getImage();
    }
}
