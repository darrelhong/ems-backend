package com.is4103.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import com.is4103.backend.config.JwtTokenUtil;
import com.is4103.backend.dto.AddCardResponse;
import com.is4103.backend.dto.AddCardRequest;
import com.is4103.backend.dto.AuthToken;
import com.is4103.backend.dto.ChangePasswordRequest;
import com.is4103.backend.dto.ChangePasswordResponse;
import com.is4103.backend.dto.DisabledAccountRequest;
import com.is4103.backend.dto.LoginRequest;
import com.is4103.backend.dto.LoginResponse;
import com.is4103.backend.dto.LoginUserResponse;
import com.is4103.backend.dto.ResetPasswordDto;
import com.is4103.backend.dto.SendEnquiryRequest;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.UpdateEmailNotiRequest;
import com.is4103.backend.dto.UpdateUserRequest;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.model.User;

import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.service.EventOrganiserService;
import com.is4103.backend.model.Event;
import com.is4103.backend.service.EventService;

import com.is4103.backend.service.RoleService;
import com.is4103.backend.service.UserService;
import com.is4103.backend.util.errors.InvalidTokenException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.registration.OnRegistrationCompleteEvent;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EventService eventService;

    @Value("${stripe.apikey}")
    private String stripeApiKey;

    @Value("${stripe.secretkey}")
    private String stripeSecretKey;

    @GetMapping(path = "/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping(value = "/login/{role}")
    public ResponseEntity<?> login(@PathVariable String role, @RequestBody LoginRequest loginRequest)
            throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // verify user has requested role
        LoginUserResponse user = userService.getUserByEmail(authentication.getName(), LoginUserResponse.class);
        Role loginRole = roleService.findByRoleEnum(RoleEnum.valueOf(role.toUpperCase()));
        Set<Role> userRoles = user.getRoles();

        if (!userRoles.contains(loginRole)) {
            throw new UserNotFoundException();
        }
        final String token = jwtTokenUtil.generateToken(authentication);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", ResponseCookie.from("token", token).maxAge(3600)
                // .httpOnly(true)
                .path("/").build().toString());
        return ResponseEntity.ok().headers(headers).body(new LoginResponse(new AuthToken(token), user));
    }

    @GetMapping(value = "/refreshtoken")
    public ResponseEntity<?> refreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String token = jwtTokenUtil.generateToken(authentication);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", ResponseCookie.from("token", token).maxAge(3600)
                // .httpOnly(true)
                .path("/").build().toString());
        return ResponseEntity.ok().headers(headers).body(new AuthToken(token));
    }

    @PostMapping(value = "/register/{role}/noverify")
    public User registerNewUserNoVerify(@PathVariable String role, @RequestBody @Valid SignupRequest signupRequest) {

        return userService.registerNewUser(signupRequest, role, true);
    }

    @PostMapping("/register/{role}")
    public User registerNewUser(@PathVariable String role, @RequestBody @Valid SignupRequest signupRequest) {

        User user = userService.registerNewUser(signupRequest, role, false);

        System.out.println("user");
        System.out.println(user);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return user;

    }

    @GetMapping("/register/confirm")
    public ModelAndView confirmRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equals("Valid Token")) {
            // redirect to login page
            return new ModelAndView("redirect:" + frontendBaseUrl + "/register/verified");
        }
        return new ModelAndView("redirect:" + frontendBaseUrl + "/register/error?&token=" + token);
    }

    @GetMapping("/register/resend")
    public User resendRegistrationToken(@RequestParam("token") String token) {
        return userService.resendToken(token);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EVNTORG', 'BIZPTNR', 'ATND')")
    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody @Valid UpdateUserRequest updateUserRequest) {
        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        // verify user id
        if (updateUserRequest.getId() != user.getId()) {
            throw new AuthenticationServiceException("An error has occured");
        }

        user = userService.updateUser(user, updateUserRequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin-update")
    public ResponseEntity<User> adminUpdateUser(@RequestBody @Valid UpdateUserRequest updateUserRequest) {
        User user = userService.getUserById(updateUserRequest.getId());
        user = userService.updateUser(user, updateUserRequest);
        return ResponseEntity.ok(user);
    }

    // @PreAuthorize("hasAnyRole('ADMIN', 'EVNTORG', 'BIZPTNR', 'ATND')")
    @PostMapping("/update-account-status")
    public ResponseEntity<User> updateAccountStatus(@RequestBody @Valid DisabledAccountRequest updateUserRequest) {
        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println("test update account");
        // verify user id
        if (updateUserRequest.getId() != user.getId()) {
            throw new AuthenticationServiceException("An error has occured");
        }

        user = userService.updateAccountStatus(user, updateUserRequest);
        System.out.println("user " + user);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EVNTORG', 'BIZPTNR', 'ATND')")
    @PostMapping("/change-password")
    public ChangePasswordResponse changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {

        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (!userService.checkOldPasswordValid(user, changePasswordRequest.getOldPassword())) {
            // return new ResponseEntity<String>("Invalid old password",
            // HttpStatus.UNAUTHORIZED);
            return new ChangePasswordResponse("Old password is incorrect.");
        }

        userService.changePassword(user, changePasswordRequest.getNewPassword());

        return new ChangePasswordResponse("Success");
        // return ResponseEntity.ok("Success");
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<String> resetPasswordRequest(@RequestParam("email") String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }
        userService.resetPasswordRequest(user);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/reset-password")
    public User resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        String result = userService.validatePasswordResetToken(resetPasswordDto.getToken());

        if (result != "Valid Token") {
            throw new InvalidTokenException(result);
        }
        return userService.savePassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/enable/{userId}")
    public User enableUser(@PathVariable Long userId) {
        return userService.enableUser(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/disable/{userId}")
    public User disable(@PathVariable Long userId) {
        return userService.disableUser(userId);
    }

    // used to protect routes
    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/userping")
    public String userPing() {
        return "Pong User";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/adminping")
    public String adminPing() {
        return "Pong Admin";
    }

    @PostMapping(value = "/disableStatus/{userId}")
    public User disableStatus(@PathVariable Long userId) {
        System.out.println("test ");
        return userService.disableUser(userId);
    }

    @PreAuthorize("hasAnyRole('EVNTORG', 'BIZPTNR', 'ATND')")
    @PostMapping(value = "/enquiry")
    public ResponseEntity sendEnquiry(@RequestBody @Valid SendEnquiryRequest sendEnquiryRequest) {
        User sender = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        // verify user id
        // System.out.println(user.getEmail());
        System.out.println(sendEnquiryRequest.getSenderEmail());
        User receiver = userService.getUserByEmail(sendEnquiryRequest.getReceiverEmail());
        System.out.println("receiver email");
        System.out.println(receiver.getEmail());
        if (!(sendEnquiryRequest.getSenderEmail().equals(sender.getEmail())) || receiver == null) {
            throw new AuthenticationServiceException("An error has occured");
        } else {

            if (sendEnquiryRequest.getEventId() != null) {
                Event event = eventService.getEventById(sendEnquiryRequest.getEventId());
                String eventName = event.getName();
                userService.sendEnquiry(sendEnquiryRequest, eventName, sender, receiver);
            } else {
                userService.sendEnquiry(sendEnquiryRequest, "", sender, receiver);
            }

        }

        return ResponseEntity.ok("Success");
    }
    @GetMapping(value = "/getPaymentMethod")
    public String getPaymentMethod() {

        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
         PaymentMethod paymentMethod = new PaymentMethod();
        if(user != null){
        
        try {
        Stripe.apiKey = stripeSecretKey;
        paymentMethod = PaymentMethod.retrieve(user.getPaymentMethodId());
            
        } catch (StripeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }      
     
        }

        return paymentMethod.toJson();
}

  @PostMapping(value = "/deleteCardPayment")
  public User deleteCard() {
      User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

  
        User userRes = userService.deletePaymentMethod(user);

        return userRes;
  }

    @PostMapping(value = "/addCardPayment")
    public AddCardResponse addCard(@RequestBody @Valid AddCardRequest addCardRequest) {
        System.out.println("call addCardPayment ");
        String resMsg = "";
        String cardholderName = addCardRequest.getCardholdername();
    
         String cardnumber = addCardRequest.getCardnumber();
 
         String cvc = addCardRequest.getCvc();
      
         String expMth = addCardRequest.getExpMth();

         String expYear = addCardRequest.getExpYear();
 

        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if(user != null){
            Stripe.apiKey = stripeApiKey;
            Map<String, Object> card = new HashMap<>();
            card.put("number", cardnumber);
            card.put("exp_month", Integer.valueOf(expMth));
            card.put("exp_year", Integer.valueOf(expYear));
            card.put("cvc", cvc);

             Map<String, Object> billing_details = new HashMap<>();
            billing_details.put("name", cardholderName);
            billing_details.put("email", user.getEmail());
            if(user.getPhonenumber() != null){
            billing_details.put("phone", user.getPhonenumber());
            }
            Map<String, Object> params = new HashMap<>();
            params.put("type", "card");
            params.put("card", card);
            params.put("billing_details",billing_details);
          
           
            PaymentMethod paymentMethod;
            try {
                paymentMethod = PaymentMethod.create(params);
                System.out.println("print payment ID");
                System.out.println(paymentMethod.getId());
                if (paymentMethod.getId() != null) {
                    User userRes = userService.savePaymentMethod(user,paymentMethod.getId());
                    if(userRes != null){
                // assigned the payment method to the customer
                Map<String, Object> params2 = new HashMap<>();
                params2.put("customer", user.getStripeCustomerId());
                paymentMethod.attach(params2);
                    resMsg = "success_added";
                    }else{
                        resMsg = "dbError";
                    }
                }

            } catch (StripeException e) {
                // TODO Auto-generated catch block
                System.out.println("error");
                System.out.println(e);
                resMsg = e.toString();

            }
        }else{
            resMsg = "dbError";
        }

       
       
         return new AddCardResponse(resMsg);
        
    }

     @PostMapping(value = "/updateEmailNoti")
    public User updateEmailNoti(@RequestBody @Valid UpdateEmailNotiRequest updateEmailNotiRequest) {
        
       boolean eoEmailNoti  = updateEmailNotiRequest.isEoEmailNoti();
      // boolean systemEmailNoti = updateEmailNotiRequest.isSystemEmailNoti();
       User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
       User userRes = userService.updateNotiSetting(user, eoEmailNoti);
        
       return userRes;
    }

}






