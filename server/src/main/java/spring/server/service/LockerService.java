package spring.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.server.domain.Locker;
import spring.server.repository.LockerRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LockerService {
    private final LockerRepository lockerRepository;

    public void create(Locker locker) {
        lockerRepository.save(locker);
    }

    public List<Locker> getLockers(Long userId) {
        return lockerRepository.findAllNotMine(userId);
    }

    public List<Locker> findAll() {
        return lockerRepository.findAll();
    }
}
