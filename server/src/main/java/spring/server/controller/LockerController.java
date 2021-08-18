package spring.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;
import spring.server.api.ApiMessage;
import spring.server.domain.Locker;
import spring.server.domain.User;
import spring.server.dto.EntrustDTO;
import spring.server.dto.LockerDTO;
import spring.server.dto.LockersDTO;
import spring.server.dto.SuccessDTO;
import spring.server.service.LockerService;
import spring.server.service.UserService;
import spring.server.token.JwtToken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/locker")
@RequiredArgsConstructor
public class LockerController {
    private final LockerService lockerService;
    private final SmartValidator smartValidator;
    private final UserService userService;

    @GetMapping("/register/{userId}")
    public String register(@PathVariable("userId") Long userId, Model model) {
        User user = new User();
        user.setId(userId);
        Locker locker = new Locker();
        locker.setUser(user);
        model.addAttribute("locker",locker);
        return "/locker/registerForm";
    }

    @PostMapping("/register")
    public String registerProcess(@ModelAttribute Locker locker, BindingResult result) {
        smartValidator.validate(locker, result);
        if (result.hasErrors()) {
            return "/locker/registerForm";
        }
        locker.setQrcode(String.valueOf(UUID.randomUUID()));
        lockerService.create(locker);
        return "redirect:/mypage/" + locker.getUser().getId();
    }

    @GetMapping("/get")
    public ResponseEntity<ApiMessage> getLockers(@RequestParam("token") String token) {
        String username = (String) JwtToken.parseJwtToken(token).get("username");
        User user = userService.findByUsername(username);
        if (user.getEntrustLocker() != null) {
            List<LockerDTO> lockers = new ArrayList<>();
            lockers.add(user.getEntrustLocker().getLockerDTO());
            return ResponseEntity.ok(ApiMessage.builder().data(new LockersDTO(true, lockers)).build());
        } else {
            List<Locker> lockers = lockerService.findAll();
            List<LockerDTO> collect = lockers.stream().map(locker -> locker.getLockerDTO())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiMessage.builder().data(new LockersDTO(false, collect)).build());
        }
    }

    @GetMapping("/info/{id}")
    public String lockerQrcode(Model model, @PathVariable("id") Long id) {
        Locker locker = lockerService.findById(id).orElseThrow();
        model.addAttribute("locker", locker);
        return "/locker/info";
    }
    @GetMapping("/storage")
    public String myLocker(Model model, @PathVariable("id") Long id) {
        Locker locker = lockerService.findById(id).orElseThrow();
        model.addAttribute("locker", locker);
        return "/locker/info";
    }

    @PostMapping("/entrust")
    public ResponseEntity<SuccessDTO> entrust(@RequestBody EntrustDTO entrustInfo) {
        Locker locker = lockerService.findByQrcode(entrustInfo.getQrcode());
        if (locker == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessDTO(false
                    , "해당하는 보관소가 없습니다."));
        }

        String username = (String) JwtToken.parseJwtToken(entrustInfo.getToken()).get("username");
        User user = userService.findByUsername(username);
        if (entrustInfo.isEntrust() && user.getEntrustLocker().equals(locker)) {
            user.setEntrustLocker(null);
            user.setEntrustTime(null);
            userService.save(user);
        } else {
            user.setEntrustTime(LocalDateTime.now());
            userService.entrust(user, locker);
        }
        return ResponseEntity.ok(new SuccessDTO(true, locker.getLockerName() + "에 짐을 맡겼습니다."));
    }
}
