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

package cc.envkeeper.app.web.rest;

import cc.envkeeper.app.EnvKeeperApp;
import cc.envkeeper.app.domain.Ticket;
import cc.envkeeper.app.domain.ProductVersion;
import cc.envkeeper.app.repository.TicketRepository;
import cc.envkeeper.app.service.TicketService;
import cc.envkeeper.app.service.dto.TicketDTO;
import cc.envkeeper.app.service.mapper.TicketMapper;
import cc.envkeeper.app.service.dto.TicketCriteria;
import cc.envkeeper.app.service.TicketQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cc.envkeeper.app.domain.enumeration.TicketType;
/**
 * Integration tests for the {@link TicketResource} REST controller.
 */
@SpringBootTest(classes = EnvKeeperApp.class)
@AutoConfigureMockMvc
@WithMockUser(roles={"USER", "WRITER"})
public class TicketResourceIT {

    private static final String DEFAULT_EXTERNAL_ID = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_ID = "BBBBBBBBBB";

    private static final TicketType DEFAULT_TICKET_TYPE = TicketType.BUG;
    private static final TicketType UPDATED_TICKET_TYPE = TicketType.EPIC;

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String DEFAULT_TICKET_URL = "AAAAAAAAAA";
    private static final String UPDATED_TICKET_URL = "BBBBBBBBBB";

    private static final String DEFAULT_PRIORITY = "AAAAAAAAAA";
    private static final String UPDATED_PRIORITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_REPORTER = "AAAAAAAAAA";
    private static final String UPDATED_REPORTER = "BBBBBBBBBB";

    private static final String DEFAULT_ASSIGNEE = "AAAAAAAAAA";
    private static final String UPDATED_ASSIGNEE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketQueryService ticketQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketMockMvc;

    private Ticket ticket;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createEntity(EntityManager em) {
        Ticket ticket = new Ticket()
            .externalId(DEFAULT_EXTERNAL_ID)
            .ticketType(DEFAULT_TICKET_TYPE)
            .summary(DEFAULT_SUMMARY)
            .ticketUrl(DEFAULT_TICKET_URL)
            .priority(DEFAULT_PRIORITY)
            .status(DEFAULT_STATUS)
            .created(DEFAULT_CREATED)
            .updated(DEFAULT_UPDATED)
            .reporter(DEFAULT_REPORTER)
            .assignee(DEFAULT_ASSIGNEE)
            .description(DEFAULT_DESCRIPTION);
        return ticket;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createUpdatedEntity(EntityManager em) {
        Ticket ticket = new Ticket()
            .externalId(UPDATED_EXTERNAL_ID)
            .ticketType(UPDATED_TICKET_TYPE)
            .summary(UPDATED_SUMMARY)
            .ticketUrl(UPDATED_TICKET_URL)
            .priority(UPDATED_PRIORITY)
            .status(UPDATED_STATUS)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED)
            .reporter(UPDATED_REPORTER)
            .assignee(UPDATED_ASSIGNEE)
            .description(UPDATED_DESCRIPTION);
        return ticket;
    }

    @BeforeEach
    public void initTest() {
        ticket = createEntity(em);
    }

    @Test
    @Transactional
    public void createTicket() throws Exception {
        int databaseSizeBeforeCreate = ticketRepository.findAll().size();
        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);
        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isCreated());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeCreate + 1);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getExternalId()).isEqualTo(DEFAULT_EXTERNAL_ID);
        assertThat(testTicket.getTicketType()).isEqualTo(DEFAULT_TICKET_TYPE);
        assertThat(testTicket.getSummary()).isEqualTo(DEFAULT_SUMMARY);
        assertThat(testTicket.getTicketUrl()).isEqualTo(DEFAULT_TICKET_URL);
        assertThat(testTicket.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testTicket.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTicket.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testTicket.getUpdated()).isEqualTo(DEFAULT_UPDATED);
        assertThat(testTicket.getReporter()).isEqualTo(DEFAULT_REPORTER);
        assertThat(testTicket.getAssignee()).isEqualTo(DEFAULT_ASSIGNEE);
        assertThat(testTicket.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createTicketWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ticketRepository.findAll().size();

        // Create the Ticket with an existing ID
        ticket.setId(1L);
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkExternalIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().size();
        // set the field null
        ticket.setExternalId(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);


        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTicketTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().size();
        // set the field null
        ticket.setTicketType(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);


        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSummaryIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().size();
        // set the field null
        ticket.setSummary(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);


        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().size();
        // set the field null
        ticket.setCreated(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);


        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUpdatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().size();
        // set the field null
        ticket.setUpdated(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);


        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTickets() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList
        restTicketMockMvc.perform(get("/api/tickets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].externalId").value(hasItem(DEFAULT_EXTERNAL_ID)))
            .andExpect(jsonPath("$.[*].ticketType").value(hasItem(DEFAULT_TICKET_TYPE.toString())))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].ticketUrl").value(hasItem(DEFAULT_TICKET_URL)))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].reporter").value(hasItem(DEFAULT_REPORTER)))
            .andExpect(jsonPath("$.[*].assignee").value(hasItem(DEFAULT_ASSIGNEE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticket.getId().intValue()))
            .andExpect(jsonPath("$.externalId").value(DEFAULT_EXTERNAL_ID))
            .andExpect(jsonPath("$.ticketType").value(DEFAULT_TICKET_TYPE.toString()))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY))
            .andExpect(jsonPath("$.ticketUrl").value(DEFAULT_TICKET_URL))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.updated").value(DEFAULT_UPDATED.toString()))
            .andExpect(jsonPath("$.reporter").value(DEFAULT_REPORTER))
            .andExpect(jsonPath("$.assignee").value(DEFAULT_ASSIGNEE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }


    @Test
    @Transactional
    public void getTicketsByIdFiltering() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        Long id = ticket.getId();

        defaultTicketShouldBeFound("id.equals=" + id);
        defaultTicketShouldNotBeFound("id.notEquals=" + id);

        defaultTicketShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTicketShouldNotBeFound("id.greaterThan=" + id);

        defaultTicketShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTicketShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllTicketsByExternalIdIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where externalId equals to DEFAULT_EXTERNAL_ID
        defaultTicketShouldBeFound("externalId.equals=" + DEFAULT_EXTERNAL_ID);

        // Get all the ticketList where externalId equals to UPDATED_EXTERNAL_ID
        defaultTicketShouldNotBeFound("externalId.equals=" + UPDATED_EXTERNAL_ID);
    }

    @Test
    @Transactional
    public void getAllTicketsByExternalIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where externalId not equals to DEFAULT_EXTERNAL_ID
        defaultTicketShouldNotBeFound("externalId.notEquals=" + DEFAULT_EXTERNAL_ID);

        // Get all the ticketList where externalId not equals to UPDATED_EXTERNAL_ID
        defaultTicketShouldBeFound("externalId.notEquals=" + UPDATED_EXTERNAL_ID);
    }

    @Test
    @Transactional
    public void getAllTicketsByExternalIdIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where externalId in DEFAULT_EXTERNAL_ID or UPDATED_EXTERNAL_ID
        defaultTicketShouldBeFound("externalId.in=" + DEFAULT_EXTERNAL_ID + "," + UPDATED_EXTERNAL_ID);

        // Get all the ticketList where externalId equals to UPDATED_EXTERNAL_ID
        defaultTicketShouldNotBeFound("externalId.in=" + UPDATED_EXTERNAL_ID);
    }

    @Test
    @Transactional
    public void getAllTicketsByExternalIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where externalId is not null
        defaultTicketShouldBeFound("externalId.specified=true");

        // Get all the ticketList where externalId is null
        defaultTicketShouldNotBeFound("externalId.specified=false");
    }
                @Test
    @Transactional
    public void getAllTicketsByExternalIdContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where externalId contains DEFAULT_EXTERNAL_ID
        defaultTicketShouldBeFound("externalId.contains=" + DEFAULT_EXTERNAL_ID);

        // Get all the ticketList where externalId contains UPDATED_EXTERNAL_ID
        defaultTicketShouldNotBeFound("externalId.contains=" + UPDATED_EXTERNAL_ID);
    }

    @Test
    @Transactional
    public void getAllTicketsByExternalIdNotContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where externalId does not contain DEFAULT_EXTERNAL_ID
        defaultTicketShouldNotBeFound("externalId.doesNotContain=" + DEFAULT_EXTERNAL_ID);

        // Get all the ticketList where externalId does not contain UPDATED_EXTERNAL_ID
        defaultTicketShouldBeFound("externalId.doesNotContain=" + UPDATED_EXTERNAL_ID);
    }


    @Test
    @Transactional
    public void getAllTicketsByTicketTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where ticketType equals to DEFAULT_TICKET_TYPE
        defaultTicketShouldBeFound("ticketType.equals=" + DEFAULT_TICKET_TYPE);

        // Get all the ticketList where ticketType equals to UPDATED_TICKET_TYPE
        defaultTicketShouldNotBeFound("ticketType.equals=" + UPDATED_TICKET_TYPE);
    }

    @Test
    @Transactional
    public void getAllTicketsByTicketTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where ticketType not equals to DEFAULT_TICKET_TYPE
        defaultTicketShouldNotBeFound("ticketType.notEquals=" + DEFAULT_TICKET_TYPE);

        // Get all the ticketList where ticketType not equals to UPDATED_TICKET_TYPE
        defaultTicketShouldBeFound("ticketType.notEquals=" + UPDATED_TICKET_TYPE);
    }

    @Test
    @Transactional
    public void getAllTicketsByTicketTypeIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where ticketType in DEFAULT_TICKET_TYPE or UPDATED_TICKET_TYPE
        defaultTicketShouldBeFound("ticketType.in=" + DEFAULT_TICKET_TYPE + "," + UPDATED_TICKET_TYPE);

        // Get all the ticketList where ticketType equals to UPDATED_TICKET_TYPE
        defaultTicketShouldNotBeFound("ticketType.in=" + UPDATED_TICKET_TYPE);
    }

    @Test
    @Transactional
    public void getAllTicketsByTicketTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where ticketType is not null
        defaultTicketShouldBeFound("ticketType.specified=true");

        // Get all the ticketList where ticketType is null
        defaultTicketShouldNotBeFound("ticketType.specified=false");
    }

    @Test
    @Transactional
    public void getAllTicketsBySummaryIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where summary equals to DEFAULT_SUMMARY
        defaultTicketShouldBeFound("summary.equals=" + DEFAULT_SUMMARY);

        // Get all the ticketList where summary equals to UPDATED_SUMMARY
        defaultTicketShouldNotBeFound("summary.equals=" + UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    public void getAllTicketsBySummaryIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where summary not equals to DEFAULT_SUMMARY
        defaultTicketShouldNotBeFound("summary.notEquals=" + DEFAULT_SUMMARY);

        // Get all the ticketList where summary not equals to UPDATED_SUMMARY
        defaultTicketShouldBeFound("summary.notEquals=" + UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    public void getAllTicketsBySummaryIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where summary in DEFAULT_SUMMARY or UPDATED_SUMMARY
        defaultTicketShouldBeFound("summary.in=" + DEFAULT_SUMMARY + "," + UPDATED_SUMMARY);

        // Get all the ticketList where summary equals to UPDATED_SUMMARY
        defaultTicketShouldNotBeFound("summary.in=" + UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    public void getAllTicketsBySummaryIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where summary is not null
        defaultTicketShouldBeFound("summary.specified=true");

        // Get all the ticketList where summary is null
        defaultTicketShouldNotBeFound("summary.specified=false");
    }
                @Test
    @Transactional
    public void getAllTicketsBySummaryContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where summary contains DEFAULT_SUMMARY
        defaultTicketShouldBeFound("summary.contains=" + DEFAULT_SUMMARY);

        // Get all the ticketList where summary contains UPDATED_SUMMARY
        defaultTicketShouldNotBeFound("summary.contains=" + UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    public void getAllTicketsBySummaryNotContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where summary does not contain DEFAULT_SUMMARY
        defaultTicketShouldNotBeFound("summary.doesNotContain=" + DEFAULT_SUMMARY);

        // Get all the ticketList where summary does not contain UPDATED_SUMMARY
        defaultTicketShouldBeFound("summary.doesNotContain=" + UPDATED_SUMMARY);
    }


    @Test
    @Transactional
    public void getAllTicketsByTicketUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where ticketUrl equals to DEFAULT_TICKET_URL
        defaultTicketShouldBeFound("ticketUrl.equals=" + DEFAULT_TICKET_URL);

        // Get all the ticketList where ticketUrl equals to UPDATED_TICKET_URL
        defaultTicketShouldNotBeFound("ticketUrl.equals=" + UPDATED_TICKET_URL);
    }

    @Test
    @Transactional
    public void getAllTicketsByTicketUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where ticketUrl not equals to DEFAULT_TICKET_URL
        defaultTicketShouldNotBeFound("ticketUrl.notEquals=" + DEFAULT_TICKET_URL);

        // Get all the ticketList where ticketUrl not equals to UPDATED_TICKET_URL
        defaultTicketShouldBeFound("ticketUrl.notEquals=" + UPDATED_TICKET_URL);
    }

    @Test
    @Transactional
    public void getAllTicketsByTicketUrlIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where ticketUrl in DEFAULT_TICKET_URL or UPDATED_TICKET_URL
        defaultTicketShouldBeFound("ticketUrl.in=" + DEFAULT_TICKET_URL + "," + UPDATED_TICKET_URL);

        // Get all the ticketList where ticketUrl equals to UPDATED_TICKET_URL
        defaultTicketShouldNotBeFound("ticketUrl.in=" + UPDATED_TICKET_URL);
    }

    @Test
    @Transactional
    public void getAllTicketsByTicketUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where ticketUrl is not null
        defaultTicketShouldBeFound("ticketUrl.specified=true");

        // Get all the ticketList where ticketUrl is null
        defaultTicketShouldNotBeFound("ticketUrl.specified=false");
    }
                @Test
    @Transactional
    public void getAllTicketsByTicketUrlContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where ticketUrl contains DEFAULT_TICKET_URL
        defaultTicketShouldBeFound("ticketUrl.contains=" + DEFAULT_TICKET_URL);

        // Get all the ticketList where ticketUrl contains UPDATED_TICKET_URL
        defaultTicketShouldNotBeFound("ticketUrl.contains=" + UPDATED_TICKET_URL);
    }

    @Test
    @Transactional
    public void getAllTicketsByTicketUrlNotContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where ticketUrl does not contain DEFAULT_TICKET_URL
        defaultTicketShouldNotBeFound("ticketUrl.doesNotContain=" + DEFAULT_TICKET_URL);

        // Get all the ticketList where ticketUrl does not contain UPDATED_TICKET_URL
        defaultTicketShouldBeFound("ticketUrl.doesNotContain=" + UPDATED_TICKET_URL);
    }


    @Test
    @Transactional
    public void getAllTicketsByPriorityIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where priority equals to DEFAULT_PRIORITY
        defaultTicketShouldBeFound("priority.equals=" + DEFAULT_PRIORITY);

        // Get all the ticketList where priority equals to UPDATED_PRIORITY
        defaultTicketShouldNotBeFound("priority.equals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    public void getAllTicketsByPriorityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where priority not equals to DEFAULT_PRIORITY
        defaultTicketShouldNotBeFound("priority.notEquals=" + DEFAULT_PRIORITY);

        // Get all the ticketList where priority not equals to UPDATED_PRIORITY
        defaultTicketShouldBeFound("priority.notEquals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    public void getAllTicketsByPriorityIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where priority in DEFAULT_PRIORITY or UPDATED_PRIORITY
        defaultTicketShouldBeFound("priority.in=" + DEFAULT_PRIORITY + "," + UPDATED_PRIORITY);

        // Get all the ticketList where priority equals to UPDATED_PRIORITY
        defaultTicketShouldNotBeFound("priority.in=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    public void getAllTicketsByPriorityIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where priority is not null
        defaultTicketShouldBeFound("priority.specified=true");

        // Get all the ticketList where priority is null
        defaultTicketShouldNotBeFound("priority.specified=false");
    }
                @Test
    @Transactional
    public void getAllTicketsByPriorityContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where priority contains DEFAULT_PRIORITY
        defaultTicketShouldBeFound("priority.contains=" + DEFAULT_PRIORITY);

        // Get all the ticketList where priority contains UPDATED_PRIORITY
        defaultTicketShouldNotBeFound("priority.contains=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    public void getAllTicketsByPriorityNotContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where priority does not contain DEFAULT_PRIORITY
        defaultTicketShouldNotBeFound("priority.doesNotContain=" + DEFAULT_PRIORITY);

        // Get all the ticketList where priority does not contain UPDATED_PRIORITY
        defaultTicketShouldBeFound("priority.doesNotContain=" + UPDATED_PRIORITY);
    }


    @Test
    @Transactional
    public void getAllTicketsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status equals to DEFAULT_STATUS
        defaultTicketShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the ticketList where status equals to UPDATED_STATUS
        defaultTicketShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllTicketsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status not equals to DEFAULT_STATUS
        defaultTicketShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the ticketList where status not equals to UPDATED_STATUS
        defaultTicketShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllTicketsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultTicketShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the ticketList where status equals to UPDATED_STATUS
        defaultTicketShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllTicketsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status is not null
        defaultTicketShouldBeFound("status.specified=true");

        // Get all the ticketList where status is null
        defaultTicketShouldNotBeFound("status.specified=false");
    }
                @Test
    @Transactional
    public void getAllTicketsByStatusContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status contains DEFAULT_STATUS
        defaultTicketShouldBeFound("status.contains=" + DEFAULT_STATUS);

        // Get all the ticketList where status contains UPDATED_STATUS
        defaultTicketShouldNotBeFound("status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllTicketsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status does not contain DEFAULT_STATUS
        defaultTicketShouldNotBeFound("status.doesNotContain=" + DEFAULT_STATUS);

        // Get all the ticketList where status does not contain UPDATED_STATUS
        defaultTicketShouldBeFound("status.doesNotContain=" + UPDATED_STATUS);
    }


    @Test
    @Transactional
    public void getAllTicketsByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where created equals to DEFAULT_CREATED
        defaultTicketShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the ticketList where created equals to UPDATED_CREATED
        defaultTicketShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllTicketsByCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where created not equals to DEFAULT_CREATED
        defaultTicketShouldNotBeFound("created.notEquals=" + DEFAULT_CREATED);

        // Get all the ticketList where created not equals to UPDATED_CREATED
        defaultTicketShouldBeFound("created.notEquals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllTicketsByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultTicketShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the ticketList where created equals to UPDATED_CREATED
        defaultTicketShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllTicketsByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where created is not null
        defaultTicketShouldBeFound("created.specified=true");

        // Get all the ticketList where created is null
        defaultTicketShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    public void getAllTicketsByUpdatedIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where updated equals to DEFAULT_UPDATED
        defaultTicketShouldBeFound("updated.equals=" + DEFAULT_UPDATED);

        // Get all the ticketList where updated equals to UPDATED_UPDATED
        defaultTicketShouldNotBeFound("updated.equals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllTicketsByUpdatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where updated not equals to DEFAULT_UPDATED
        defaultTicketShouldNotBeFound("updated.notEquals=" + DEFAULT_UPDATED);

        // Get all the ticketList where updated not equals to UPDATED_UPDATED
        defaultTicketShouldBeFound("updated.notEquals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllTicketsByUpdatedIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where updated in DEFAULT_UPDATED or UPDATED_UPDATED
        defaultTicketShouldBeFound("updated.in=" + DEFAULT_UPDATED + "," + UPDATED_UPDATED);

        // Get all the ticketList where updated equals to UPDATED_UPDATED
        defaultTicketShouldNotBeFound("updated.in=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllTicketsByUpdatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where updated is not null
        defaultTicketShouldBeFound("updated.specified=true");

        // Get all the ticketList where updated is null
        defaultTicketShouldNotBeFound("updated.specified=false");
    }

    @Test
    @Transactional
    public void getAllTicketsByReporterIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where reporter equals to DEFAULT_REPORTER
        defaultTicketShouldBeFound("reporter.equals=" + DEFAULT_REPORTER);

        // Get all the ticketList where reporter equals to UPDATED_REPORTER
        defaultTicketShouldNotBeFound("reporter.equals=" + UPDATED_REPORTER);
    }

    @Test
    @Transactional
    public void getAllTicketsByReporterIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where reporter not equals to DEFAULT_REPORTER
        defaultTicketShouldNotBeFound("reporter.notEquals=" + DEFAULT_REPORTER);

        // Get all the ticketList where reporter not equals to UPDATED_REPORTER
        defaultTicketShouldBeFound("reporter.notEquals=" + UPDATED_REPORTER);
    }

    @Test
    @Transactional
    public void getAllTicketsByReporterIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where reporter in DEFAULT_REPORTER or UPDATED_REPORTER
        defaultTicketShouldBeFound("reporter.in=" + DEFAULT_REPORTER + "," + UPDATED_REPORTER);

        // Get all the ticketList where reporter equals to UPDATED_REPORTER
        defaultTicketShouldNotBeFound("reporter.in=" + UPDATED_REPORTER);
    }

    @Test
    @Transactional
    public void getAllTicketsByReporterIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where reporter is not null
        defaultTicketShouldBeFound("reporter.specified=true");

        // Get all the ticketList where reporter is null
        defaultTicketShouldNotBeFound("reporter.specified=false");
    }
                @Test
    @Transactional
    public void getAllTicketsByReporterContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where reporter contains DEFAULT_REPORTER
        defaultTicketShouldBeFound("reporter.contains=" + DEFAULT_REPORTER);

        // Get all the ticketList where reporter contains UPDATED_REPORTER
        defaultTicketShouldNotBeFound("reporter.contains=" + UPDATED_REPORTER);
    }

    @Test
    @Transactional
    public void getAllTicketsByReporterNotContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where reporter does not contain DEFAULT_REPORTER
        defaultTicketShouldNotBeFound("reporter.doesNotContain=" + DEFAULT_REPORTER);

        // Get all the ticketList where reporter does not contain UPDATED_REPORTER
        defaultTicketShouldBeFound("reporter.doesNotContain=" + UPDATED_REPORTER);
    }


    @Test
    @Transactional
    public void getAllTicketsByAssigneeIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assignee equals to DEFAULT_ASSIGNEE
        defaultTicketShouldBeFound("assignee.equals=" + DEFAULT_ASSIGNEE);

        // Get all the ticketList where assignee equals to UPDATED_ASSIGNEE
        defaultTicketShouldNotBeFound("assignee.equals=" + UPDATED_ASSIGNEE);
    }

    @Test
    @Transactional
    public void getAllTicketsByAssigneeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assignee not equals to DEFAULT_ASSIGNEE
        defaultTicketShouldNotBeFound("assignee.notEquals=" + DEFAULT_ASSIGNEE);

        // Get all the ticketList where assignee not equals to UPDATED_ASSIGNEE
        defaultTicketShouldBeFound("assignee.notEquals=" + UPDATED_ASSIGNEE);
    }

    @Test
    @Transactional
    public void getAllTicketsByAssigneeIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assignee in DEFAULT_ASSIGNEE or UPDATED_ASSIGNEE
        defaultTicketShouldBeFound("assignee.in=" + DEFAULT_ASSIGNEE + "," + UPDATED_ASSIGNEE);

        // Get all the ticketList where assignee equals to UPDATED_ASSIGNEE
        defaultTicketShouldNotBeFound("assignee.in=" + UPDATED_ASSIGNEE);
    }

    @Test
    @Transactional
    public void getAllTicketsByAssigneeIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assignee is not null
        defaultTicketShouldBeFound("assignee.specified=true");

        // Get all the ticketList where assignee is null
        defaultTicketShouldNotBeFound("assignee.specified=false");
    }
                @Test
    @Transactional
    public void getAllTicketsByAssigneeContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assignee contains DEFAULT_ASSIGNEE
        defaultTicketShouldBeFound("assignee.contains=" + DEFAULT_ASSIGNEE);

        // Get all the ticketList where assignee contains UPDATED_ASSIGNEE
        defaultTicketShouldNotBeFound("assignee.contains=" + UPDATED_ASSIGNEE);
    }

    @Test
    @Transactional
    public void getAllTicketsByAssigneeNotContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where assignee does not contain DEFAULT_ASSIGNEE
        defaultTicketShouldNotBeFound("assignee.doesNotContain=" + DEFAULT_ASSIGNEE);

        // Get all the ticketList where assignee does not contain UPDATED_ASSIGNEE
        defaultTicketShouldBeFound("assignee.doesNotContain=" + UPDATED_ASSIGNEE);
    }


    @Test
    @Transactional
    public void getAllTicketsByAffectsIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);
        ProductVersion affects = ProductVersionResourceIT.createEntity(em);
        em.persist(affects);
        em.flush();
        ticket.setAffects(affects);
        ticketRepository.saveAndFlush(ticket);
        Long affectsId = affects.getId();

        // Get all the ticketList where affects equals to affectsId
        defaultTicketShouldBeFound("affectsId.equals=" + affectsId);

        // Get all the ticketList where affects equals to affectsId + 1
        defaultTicketShouldNotBeFound("affectsId.equals=" + (affectsId + 1));
    }


    @Test
    @Transactional
    public void getAllTicketsByFixedInIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);
        ProductVersion fixedIn = ProductVersionResourceIT.createEntity(em);
        em.persist(fixedIn);
        em.flush();
        ticket.setFixedIn(fixedIn);
        ticketRepository.saveAndFlush(ticket);
        Long fixedInId = fixedIn.getId();

        // Get all the ticketList where fixedIn equals to fixedInId
        defaultTicketShouldBeFound("fixedInId.equals=" + fixedInId);

        // Get all the ticketList where fixedIn equals to fixedInId + 1
        defaultTicketShouldNotBeFound("fixedInId.equals=" + (fixedInId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTicketShouldBeFound(String filter) throws Exception {
        restTicketMockMvc.perform(get("/api/tickets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].externalId").value(hasItem(DEFAULT_EXTERNAL_ID)))
            .andExpect(jsonPath("$.[*].ticketType").value(hasItem(DEFAULT_TICKET_TYPE.toString())))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].ticketUrl").value(hasItem(DEFAULT_TICKET_URL)))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].reporter").value(hasItem(DEFAULT_REPORTER)))
            .andExpect(jsonPath("$.[*].assignee").value(hasItem(DEFAULT_ASSIGNEE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));

        // Check, that the count call also returns 1
        restTicketMockMvc.perform(get("/api/tickets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTicketShouldNotBeFound(String filter) throws Exception {
        restTicketMockMvc.perform(get("/api/tickets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTicketMockMvc.perform(get("/api/tickets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingTicket() throws Exception {
        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();

        // Update the ticket
        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).get();
        // Disconnect from session so that the updates on updatedTicket are not directly saved in db
        em.detach(updatedTicket);
        updatedTicket
            .externalId(UPDATED_EXTERNAL_ID)
            .ticketType(UPDATED_TICKET_TYPE)
            .summary(UPDATED_SUMMARY)
            .ticketUrl(UPDATED_TICKET_URL)
            .priority(UPDATED_PRIORITY)
            .status(UPDATED_STATUS)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED)
            .reporter(UPDATED_REPORTER)
            .assignee(UPDATED_ASSIGNEE)
            .description(UPDATED_DESCRIPTION);
        TicketDTO ticketDTO = ticketMapper.toDto(updatedTicket);

        restTicketMockMvc.perform(put("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isOk());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getExternalId()).isEqualTo(UPDATED_EXTERNAL_ID);
        assertThat(testTicket.getTicketType()).isEqualTo(UPDATED_TICKET_TYPE);
        assertThat(testTicket.getSummary()).isEqualTo(UPDATED_SUMMARY);
        assertThat(testTicket.getTicketUrl()).isEqualTo(UPDATED_TICKET_URL);
        assertThat(testTicket.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testTicket.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTicket.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testTicket.getUpdated()).isEqualTo(UPDATED_UPDATED);
        assertThat(testTicket.getReporter()).isEqualTo(UPDATED_REPORTER);
        assertThat(testTicket.getAssignee()).isEqualTo(UPDATED_ASSIGNEE);
        assertThat(testTicket.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketMockMvc.perform(put("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        int databaseSizeBeforeDelete = ticketRepository.findAll().size();

        // Delete the ticket
        restTicketMockMvc.perform(delete("/api/tickets/{id}", ticket.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
