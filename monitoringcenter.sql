--
-- PostgreSQL database dump
--

\restrict DRAdkzbetS7ftvmVm1mTRqqu78jfVEFi7vFtvQpsfK5PDD6iUwXMw1BbNApAw6M

-- Dumped from database version 17.6
-- Dumped by pg_dump version 17.6

-- Started on 2025-11-01 16:32:17

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 16964)
-- Name: carbrands; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.carbrands (
    idbrand bigint NOT NULL,
    name character varying(30) NOT NULL
);


ALTER TABLE public.carbrands OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16967)
-- Name: carmodels; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.carmodels (
    modelid bigint NOT NULL,
    name character varying(30) NOT NULL,
    carbrand bigint NOT NULL
);


ALTER TABLE public.carmodels OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16986)
-- Name: cars; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cars (
    carid bigint NOT NULL,
    carbrand bigint NOT NULL,
    carmodel bigint NOT NULL,
    "licensePlate" character varying(20) NOT NULL,
    owner character varying(20) NOT NULL,
    address character varying(20) NOT NULL,
    colour character varying NOT NULL
);


ALTER TABLE public.cars OWNER TO postgres;

--
-- TOC entry 4903 (class 0 OID 16964)
-- Dependencies: 217
-- Data for Name: carbrands; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.carbrands (idbrand, name) FROM stdin;
1	Volkswagen
2	Audi
3	Fiat
4	Renault
5	Chevrolet
6	Ford
\.


--
-- TOC entry 4904 (class 0 OID 16967)
-- Dependencies: 218
-- Data for Name: carmodels; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.carmodels (modelid, name, carbrand) FROM stdin;
\.


--
-- TOC entry 4905 (class 0 OID 16986)
-- Dependencies: 219
-- Data for Name: cars; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cars (carid, carbrand, carmodel, "licensePlate", owner, address, colour) FROM stdin;
\.


--
-- TOC entry 4750 (class 2606 OID 16971)
-- Name: carbrands carbrands_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carbrands
    ADD CONSTRAINT carbrands_pkey PRIMARY KEY (idbrand);


--
-- TOC entry 4752 (class 2606 OID 16973)
-- Name: carmodels carmodels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carmodels
    ADD CONSTRAINT carmodels_pkey PRIMARY KEY (modelid);


--
-- TOC entry 4754 (class 2606 OID 16992)
-- Name: cars cars_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT cars_pkey PRIMARY KEY (carid);


--
-- TOC entry 4756 (class 2606 OID 16993)
-- Name: cars fk_carbrand_car; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT fk_carbrand_car FOREIGN KEY (carbrand) REFERENCES public.carbrands(idbrand) NOT VALID;


--
-- TOC entry 4755 (class 2606 OID 16974)
-- Name: carmodels fk_carbrand_carmodel; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carmodels
    ADD CONSTRAINT fk_carbrand_carmodel FOREIGN KEY (carbrand) REFERENCES public.carbrands(idbrand) NOT VALID;


--
-- TOC entry 4757 (class 2606 OID 16998)
-- Name: cars fk_carmodel_car; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT fk_carmodel_car FOREIGN KEY (carmodel) REFERENCES public.carmodels(modelid) NOT VALID;


-- Completed on 2025-11-01 16:32:17

--
-- PostgreSQL database dump complete
--

\unrestrict DRAdkzbetS7ftvmVm1mTRqqu78jfVEFi7vFtvQpsfK5PDD6iUwXMw1BbNApAw6M

