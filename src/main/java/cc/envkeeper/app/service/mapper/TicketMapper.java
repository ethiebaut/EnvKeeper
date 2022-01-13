/*
 * Copyright (c) 2022 Eric Thiebaut-George.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cc.envkeeper.app.service.mapper;


import cc.envkeeper.app.domain.*;
import cc.envkeeper.app.service.dto.TicketDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ticket} and its DTO {@link TicketDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProductVersionMapper.class})
public interface TicketMapper extends EntityMapper<TicketDTO, Ticket> {

    @Mapping(source = "affects.id", target = "affectsId")
    @Mapping(source = "affects.version", target = "affectsVersion")
    @Mapping(source = "fixedIn.id", target = "fixedInId")
    @Mapping(source = "fixedIn.version", target = "fixedInVersion")
    @Mapping(source = "affects.product.id", target = "affectsProductId")
    @Mapping(source = "affects.product.shortName", target = "affectsProductShortname")
    @Mapping(source = "fixedIn.product.id", target = "fixedInProductId")
    @Mapping(source = "fixedIn.product.shortName", target = "fixedInProductShortname")
    TicketDTO toDto(Ticket ticket);

    @Mapping(source = "affectsId", target = "affects")
    @Mapping(source = "fixedInId", target = "fixedIn")
    Ticket toEntity(TicketDTO ticketDTO);

    default Ticket fromId(Long id) {
        if (id == null) {
            return null;
        }
        Ticket ticket = new Ticket();
        ticket.setId(id);
        return ticket;
    }
}
