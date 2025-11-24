package com.unimag.passengerservice.service.impl;

import com.unimag.passengerservice.dto.request.CreatePassengerRequestDTO;
import com.unimag.passengerservice.dto.request.UpdatePassengerRequestDTO;
import com.unimag.passengerservice.dto.response.PassengerResponseDTO;
import com.unimag.passengerservice.entity.Passenger;
import com.unimag.passengerservice.exception.alreadyexists.PassengerWithEmailAlreadyExistsException;
import com.unimag.passengerservice.exception.notfound.PassengerNotFoundException;
import com.unimag.passengerservice.mapper.PassengerMapper;
import com.unimag.passengerservice.repository.PassengerRepository;
import com.unimag.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    public Mono<PassengerResponseDTO> getById(Long id) {
        log.debug("Getting passenger by id: {}", id);

        return passengerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException("Passenger not found with id: " + id)))
                .map(passengerMapper::toDTO);
    }

    @Override
    public Flux<PassengerResponseDTO> getAll() {
        log.debug("Getting all passengers");

        return passengerRepository.findAll()
                .map(passengerMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<PassengerResponseDTO> create(CreatePassengerRequestDTO request) {
        log.info("Creating passenger with email: {}", request.email());

        return passengerRepository.existsByEmail(request.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new PassengerWithEmailAlreadyExistsException("Email already registered"));
                    }
                    Passenger passenger = passengerMapper.toEntity(request);
                    return passengerRepository.save(passenger);
                }). map(passengerMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<PassengerResponseDTO> update(Long id, UpdatePassengerRequestDTO request) {
        log.info("Updating passenger with id: {}", id);

        return passengerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException("Passenger not found with id: " + id)))
                .flatMap(passenger -> {
                    if(request.name()!=null) {
                        passenger.setName(request.name());
                    }
                    if(request.email()!=null) {
                        passenger.setEmail(request.email());
                    }

                    return passengerRepository.save(passenger);
                }).map(passengerMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<Void> delete(Long id) {
        log.warn("Deleting passenger with id: {}", id);

        return passengerRepository.existsById(id)
                .flatMap(exists -> {
                    if(!exists) {
                        return Mono.error(
                                new PassengerNotFoundException("Passenger not found with id: " + id));
                    }
                    return passengerRepository.deleteById(id);
                });
    }
}
