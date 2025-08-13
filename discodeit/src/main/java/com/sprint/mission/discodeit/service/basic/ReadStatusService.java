package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("readStatusService")
@RequiredArgsConstructor
public class ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    public ReadStatus create(BinaryContentCreateRequest request){
        if( userRepository.findAll().contains(request.id())){
            for(ReadStatus read : readStatusRepository.findAll()){
                if(read.getUserId().equals(request.id())){
                    throw new RuntimeException("user already exists");
                }
                readStatusRepository.save(new ReadStatus(userRepository.findById(request.id())
                        .orElseThrow(()->new RuntimeException("user not exists"))));
            }

        }else if(channelRepository.findAll().contains(request.id())){
            for(ReadStatus read : readStatusRepository.findAll()){
                if(read.getChannelId().equals(request.id())){
                    throw new RuntimeException("channel already exists");
                }
                readStatusRepository.save(new ReadStatus(channelRepository.findById(request.id())
                        .orElseThrow(()->new RuntimeException("channel not exists"))));
            }

        } else{
            throw new RuntimeException("관련된 Channel이나 User가 존재하지 않습니다.");
        }

        return readStatusRepository.findById(request.id());
    }


    public ReadStatus find(UUID id){
        return readStatusRepository.findById(id);
    }


    public List<ReadStatus> findAllByUserId(UUID id){
        List<ReadStatus> status=new ArrayList<>();
        for(ReadStatus read : readStatusRepository.findAll()){
            if(read.getUserId().equals(id)){
                status.add(read);
            }
        }
        if(status.isEmpty()){
            return new ArrayList<>();
        }
        return status;
    }


    public ReadStatus update(ReadStatusUpdateRequest request){
        ReadStatus readStatus = readStatusRepository.findById(request.id());
        if(readStatus.getReadTime() != null || readStatus.getReadTime().isBefore(request.readTime()) ){
            readStatusRepository.findById(request.id()).setReadTime(request.readTime());
        }
        return readStatusRepository.findById(request.id());

    }

    public void delete(UUID id){
        readStatusRepository.deleteById(id);
    }







}
