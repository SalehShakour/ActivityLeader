package com.example.redistest.service;

import com.example.redistest.model.User;
import com.example.redistest.repository.UserRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    final UserRepository userRepository;
    private final RedissonClient redissonClient;

    public UserService(UserRepository userRepository, RedissonClient redissonClient) {
        this.userRepository = userRepository;
        this.redissonClient = redissonClient;
    }

    public void addActivity(User currentUser) {
        String userId = String.valueOf(currentUser.getId());
        RLock lock = redissonClient.getLock("userLock:" + userId);

        try {
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                currentUser.setActivities(currentUser.getActivities() + 1);
                userRepository.save(currentUser);
            } else {
                LOG.warn("Failed to acquire lock for user: {}", userId);
            }
        } catch (InterruptedException e) {
            LOG.error("Error while acquiring lock for user: {}", userId, e);
        } finally {
            lock.unlock();
        }
    }


    @CacheEvict(value = "cache", allEntries = true)
    @Scheduled(fixedDelayString = "${caching.spring.userListTTL}", initialDelay = 10000)
    public void deleteUserList()  {
        LOG.info("Evict User List");
    }

    @Scheduled(fixedDelayString = "60000")
    public void leaderBoard() {
        RLock lock = redissonClient.getLock("leaderBoardLock");

        try {
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                List<User> topUser = getTopUser();

                if (topUser != null) {
                    for (int i=0; i<topUser.size() && i <= 10; i++){
                        LOG.info("Top User: {} with {} activities", topUser.get(i).getUsername(), topUser.get(i).getActivities());
                    }
                } else {
                    LOG.info("No users found.");
                }
            } else {
                LOG.warn("Failed to acquire leader board lock");
            }
        } catch (InterruptedException e) {
            LOG.error("Error while acquiring leader board lock", e);
        } finally {
            lock.unlock();
        }
    }

    private List<User> getTopUser() {
        return userRepository.findAllByOrderByActivitiesDesc();
    }



}
