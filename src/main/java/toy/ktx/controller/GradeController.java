package toy.ktx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.PassengerDto;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.dto.projections.VipSeatDto;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.*;
import toy.ktx.service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GradeController {

    private final DeployService deployService;
    private final KtxService ktxService;
    private final KtxRoomService ktxRoomService;
    private final KtxSeatNormalService ktxSeatNormalService;
    private final KtxSeatVipService ktxSeatVipService;

    //동시성 제어
    private ThreadLocal<List<String>> okList = new ThreadLocal<>();

    @PostMapping("/grade")
    public String choiceGrade(@ModelAttribute DeployForm deployForm,
                              @ModelAttribute PassengerDto passengerDto,
                              @RequestParam(required = false) Boolean round,
                              @RequestParam(required = false) Boolean going,
                              @RequestParam(required = false) Boolean coming,
                              @RequestParam(required = false) String departurePlace,
                              @RequestParam(required = false) String arrivalPlace,
                              @RequestParam(required = false) String dateTimeOfGoing,
                              @RequestParam(required = false) String dateTimeOfLeaving,
                              @RequestParam(required = false) String normal,
                              @RequestParam(required = false) String vip,
                              @RequestParam(required = false) Boolean normalDisabled,
                              @RequestParam(required = false) Boolean vipDisabled,
                              Model model) {

        model.addAttribute("departurePlace", departurePlace);
        model.addAttribute("arrivalPlace", arrivalPlace);
        model.addAttribute("passengers", passengerDto.howManyOccupied());
        //해당 없으면 null로 들어옴
        model.addAttribute("normalDisabled", normalDisabled);
        model.addAttribute("vipDisabled", vipDisabled);

        okList.set(new ArrayList<>());

        if (normal != null && round == true && coming == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Deploy deploy = deployService.getDeployWithTrain(deployForm.getDeployIdOfComing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatByGradeFetch(ktx, Grade.NORMAL);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatNormal ktxSeatNormal = (KtxSeatNormal) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }
            log.info("fuck = {}", okList.get());

            NormalSeatDto normalSeatDto = ktxSeatNormalService.findNormalDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("coming", true);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());
            okList.remove();

            return "chooseNormalSeat";
        }

        else if (normal != null && round == true) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Deploy deploy = deployService.getDeployWithTrain(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatByGradeFetch(ktx, Grade.NORMAL);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatNormal ktxSeatNormal = (KtxSeatNormal) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }
            log.info("fuck = {}", okList.get());

            NormalSeatDto normalSeatDto = ktxSeatNormalService.findNormalDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());
            okList.remove();

            return "chooseNormalSeat";
        }

        if (normal != null) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);

            Deploy deploy = deployService.getDeployWithTrain(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatByGradeFetch(ktx, Grade.NORMAL);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatNormal ktxSeatNormal = (KtxSeatNormal) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }
            log.info("fuck1234 = {}", okList.get());

            NormalSeatDto normalSeatDto = ktxSeatNormalService.findNormalDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());
            okList.remove();

            return "chooseNormalSeat";
        }

// normal vs vip -------------------------------------------------------------------------------------------------------------------------------------
        if (vip != null && round == true && coming == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Deploy deploy = deployService.getDeployWithTrain(deployForm.getDeployIdOfComing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatByGradeFetch(ktx, Grade.VIP);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatVip ktxSeatNormal = (KtxSeatVip) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }
            log.info("fuck = {}", okList.get());

            VipSeatDto vipSeatDto = ktxSeatVipService.findVipDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("coming", true);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());
            okList.remove();

            return "chooseVipSeat";
        }

        else if (vip != null && round == true) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Deploy deploy = deployService.getDeployWithTrain(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatByGradeFetch(ktx, Grade.VIP);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatVip ktxSeatNormal = (KtxSeatVip) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }
            log.info("fuck = {}", okList.get());

            VipSeatDto vipSeatDto = ktxSeatVipService.findVipDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());
            okList.remove();

            return "chooseVipSeat";
        }

        else {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);

            Deploy deploy = deployService.getDeployWithTrain(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatByGradeFetch(ktx, Grade.NORMAL);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatVip ktxSeatNormal = (KtxSeatVip) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }
            log.info("fuck = {}", okList.get());

            VipSeatDto vipSeatDto = ktxSeatVipService.findVipDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());
            okList.remove();

            return "chooseVipSeat";
        }
    }

    private LocalDateTime getLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTime, formatter);
    }
}
