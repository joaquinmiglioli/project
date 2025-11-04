--
-- PostgreSQL database dump
--

\restrict puNFULCfmdzyvQqAod3Q0pGIaAb9K4jATXApk4agiATOPcrL7VpTioGgQ2aa713

-- Dumped from database version 18.0
-- Dumped by pg_dump version 18.0

-- Started on 2025-11-03 22:26:01

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
-- TOC entry 220 (class 1259 OID 24796)
-- Name: carbrands; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.carbrands (
    idbrand bigint NOT NULL,
    name character varying(30) NOT NULL
);


ALTER TABLE public.carbrands OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 24795)
-- Name: carbrands_idbrand_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.carbrands_idbrand_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.carbrands_idbrand_seq OWNER TO postgres;

--
-- TOC entry 4955 (class 0 OID 0)
-- Dependencies: 219
-- Name: carbrands_idbrand_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.carbrands_idbrand_seq OWNED BY public.carbrands.idbrand;


--
-- TOC entry 222 (class 1259 OID 24805)
-- Name: carmodels; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.carmodels (
    modelid bigint NOT NULL,
    name character varying(30) NOT NULL,
    carbrand bigint NOT NULL
);


ALTER TABLE public.carmodels OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 24804)
-- Name: carmodels_modelid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.carmodels_modelid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.carmodels_modelid_seq OWNER TO postgres;

--
-- TOC entry 4956 (class 0 OID 0)
-- Dependencies: 221
-- Name: carmodels_modelid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.carmodels_modelid_seq OWNED BY public.carmodels.modelid;


--
-- TOC entry 224 (class 1259 OID 24820)
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
-- TOC entry 223 (class 1259 OID 24819)
-- Name: cars_carid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.cars_carid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.cars_carid_seq OWNER TO postgres;

--
-- TOC entry 4957 (class 0 OID 0)
-- Dependencies: 223
-- Name: cars_carid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.cars_carid_seq OWNED BY public.cars.carid;


--
-- TOC entry 226 (class 1259 OID 24855)
-- Name: fines; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.fines (
    fineid bigint NOT NULL,
    finedate timestamp with time zone DEFAULT now() NOT NULL,
    type character varying(20) NOT NULL,
    amount numeric(12,2) NOT NULL,
    scoringpoints integer NOT NULL,
    deviceid character varying(40),
    photourl text,
    barcode character varying(18),
    carid bigint NOT NULL
);


ALTER TABLE public.fines OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 24854)
-- Name: fines_fineid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.fines_fineid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.fines_fineid_seq OWNER TO postgres;

--
-- TOC entry 4958 (class 0 OID 0)
-- Dependencies: 225
-- Name: fines_fineid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.fines_fineid_seq OWNED BY public.fines.fineid;


--
-- TOC entry 4770 (class 2604 OID 24799)
-- Name: carbrands idbrand; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carbrands ALTER COLUMN idbrand SET DEFAULT nextval('public.carbrands_idbrand_seq'::regclass);


--
-- TOC entry 4771 (class 2604 OID 24808)
-- Name: carmodels modelid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carmodels ALTER COLUMN modelid SET DEFAULT nextval('public.carmodels_modelid_seq'::regclass);


--
-- TOC entry 4772 (class 2604 OID 24823)
-- Name: cars carid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars ALTER COLUMN carid SET DEFAULT nextval('public.cars_carid_seq'::regclass);


--
-- TOC entry 4773 (class 2604 OID 24858)
-- Name: fines fineid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fines ALTER COLUMN fineid SET DEFAULT nextval('public.fines_fineid_seq'::regclass);


--
-- TOC entry 4943 (class 0 OID 24796)
-- Dependencies: 220
-- Data for Name: carbrands; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.carbrands VALUES (1, 'Volkswagen');
INSERT INTO public.carbrands VALUES (2, 'Audi');
INSERT INTO public.carbrands VALUES (3, 'Fiat');
INSERT INTO public.carbrands VALUES (4, 'Renault');
INSERT INTO public.carbrands VALUES (5, 'Chevrolet');
INSERT INTO public.carbrands VALUES (6, 'Ford');


--
-- TOC entry 4945 (class 0 OID 24805)
-- Dependencies: 222
-- Data for Name: carmodels; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.carmodels VALUES (1, 'Gol', 1);
INSERT INTO public.carmodels VALUES (2, 'Polo', 1);
INSERT INTO public.carmodels VALUES (3, 'A3', 2);
INSERT INTO public.carmodels VALUES (4, 'A4', 2);
INSERT INTO public.carmodels VALUES (5, 'Palio', 3);
INSERT INTO public.carmodels VALUES (6, 'Cronos', 3);
INSERT INTO public.carmodels VALUES (7, 'Clio', 4);
INSERT INTO public.carmodels VALUES (8, 'Sandero', 4);
INSERT INTO public.carmodels VALUES (9, 'Onix', 5);
INSERT INTO public.carmodels VALUES (10, 'Cruze', 5);
INSERT INTO public.carmodels VALUES (11, 'Fiesta', 6);
INSERT INTO public.carmodels VALUES (12, 'Focus', 6);


--
-- TOC entry 4947 (class 0 OID 24820)
-- Dependencies: 224
-- Data for Name: cars; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.cars VALUES (1, 1, 1, 'AB123CD', 'Juan Perez', 'Av Siempreviva 742', 'Rojo');
INSERT INTO public.cars VALUES (2, 1, 2, 'CD456EF', 'Lucia Gomez', 'Belgrano 123', 'Negro');
INSERT INTO public.cars VALUES (3, 2, 3, 'EF789GH', 'Martin Ruiz', 'Mitre 890', 'Gris');
INSERT INTO public.cars VALUES (4, 3, 5, 'GH321IJ', 'Ana Torres', 'San Martin 77', 'Blanco');
INSERT INTO public.cars VALUES (5, 5, 9, 'IJ654KL', 'Pedro Rios', 'Córdoba 555', 'Azul');
INSERT INTO public.cars VALUES (6, 6, 11, 'KL987MN', 'Maria Lopez', 'Rivadavia 400', 'Rojo');
INSERT INTO public.cars VALUES (7, 1, 1, 'AB123CE', 'Juan Perez', 'Av Siempreviva 742', 'Rojo');
INSERT INTO public.cars VALUES (8, 1, 2, 'CD456ED', 'Lucia Gomez', 'Belgrano 123', 'Negro');
INSERT INTO public.cars VALUES (9, 2, 3, 'EF789GG', 'Martin Ruiz', 'Mitre 890', 'Gris');
INSERT INTO public.cars VALUES (10, 3, 5, 'GH321IK', 'Ana Torres', 'San Martin 77', 'Blanco');
INSERT INTO public.cars VALUES (11, 5, 9, 'IJ654KP', 'Pedro Rios', 'Córdoba 555', 'Azul');
INSERT INTO public.cars VALUES (12, 6, 11, 'KL987MM', 'Maria Lopez', 'Rivadavia 400', 'Rojo');


--
-- TOC entry 4949 (class 0 OID 24855)
-- Dependencies: 226
-- Data for Name: fines; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.fines VALUES (2, '2025-11-03 22:19:59.731122-03', 'PARKING', 32000.00, 2, 'Parking Camera 3', 'FinesPhoto1.jpeg', '000002000003200000', 9);
INSERT INTO public.fines VALUES (4, '2025-11-03 22:20:23.472529-03', 'RED_LIGHT', 80000.00, 5, 'Semaphore 23', 'FinesPhoto3.jpg', '000004000008000000', 11);
INSERT INTO public.fines VALUES (1, '2025-11-03 22:14:29.367434-03', 'RED_LIGHT', 80000.00, 5, 'Semaphore 9', 'FinesPhoto3.jpg', '000001000008000000', 2);
INSERT INTO public.fines VALUES (3, '2025-11-03 22:20:12.968895-03', 'RED_LIGHT', 80000.00, 5, 'Semaphore 10', 'FinesPhoto2.jpg', '000003000008000000', 10);


--
-- TOC entry 4959 (class 0 OID 0)
-- Dependencies: 219
-- Name: carbrands_idbrand_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.carbrands_idbrand_seq', 6, true);


--
-- TOC entry 4960 (class 0 OID 0)
-- Dependencies: 221
-- Name: carmodels_modelid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.carmodels_modelid_seq', 12, true);


--
-- TOC entry 4961 (class 0 OID 0)
-- Dependencies: 223
-- Name: cars_carid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.cars_carid_seq', 12, true);


--
-- TOC entry 4962 (class 0 OID 0)
-- Dependencies: 225
-- Name: fines_fineid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.fines_fineid_seq', 4, true);


--
-- TOC entry 4776 (class 2606 OID 24803)
-- Name: carbrands carbrands_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carbrands
    ADD CONSTRAINT carbrands_pkey PRIMARY KEY (idbrand);


--
-- TOC entry 4778 (class 2606 OID 24813)
-- Name: carmodels carmodels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carmodels
    ADD CONSTRAINT carmodels_pkey PRIMARY KEY (modelid);


--
-- TOC entry 4781 (class 2606 OID 24834)
-- Name: cars cars_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT cars_pkey PRIMARY KEY (carid);


--
-- TOC entry 4787 (class 2606 OID 24869)
-- Name: fines fines_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fines
    ADD CONSTRAINT fines_pkey PRIMARY KEY (fineid);


--
-- TOC entry 4785 (class 2606 OID 24850)
-- Name: cars uq_cars_licenseplate; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT uq_cars_licenseplate UNIQUE ("licensePlate");


--
-- TOC entry 4782 (class 1259 OID 24851)
-- Name: idx_cars_brand; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cars_brand ON public.cars USING btree (carbrand);


--
-- TOC entry 4783 (class 1259 OID 24852)
-- Name: idx_cars_model; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cars_model ON public.cars USING btree (carmodel);


--
-- TOC entry 4788 (class 1259 OID 24875)
-- Name: idx_fines_carid; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_fines_carid ON public.fines USING btree (carid);


--
-- TOC entry 4789 (class 1259 OID 24877)
-- Name: idx_fines_date; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_fines_date ON public.fines USING btree (finedate);


--
-- TOC entry 4790 (class 1259 OID 24876)
-- Name: idx_fines_type; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_fines_type ON public.fines USING btree (type);


--
-- TOC entry 4779 (class 1259 OID 24853)
-- Name: idx_models_brand; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_models_brand ON public.carmodels USING btree (carbrand);


--
-- TOC entry 4791 (class 2606 OID 24814)
-- Name: carmodels carmodels_carbrand_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carmodels
    ADD CONSTRAINT carmodels_carbrand_fkey FOREIGN KEY (carbrand) REFERENCES public.carbrands(idbrand);


--
-- TOC entry 4792 (class 2606 OID 24835)
-- Name: cars cars_carbrand_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT cars_carbrand_fkey FOREIGN KEY (carbrand) REFERENCES public.carbrands(idbrand);


--
-- TOC entry 4793 (class 2606 OID 24840)
-- Name: cars cars_carmodel_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT cars_carmodel_fkey FOREIGN KEY (carmodel) REFERENCES public.carmodels(modelid);


--
-- TOC entry 4794 (class 2606 OID 24870)
-- Name: fines fines_carid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fines
    ADD CONSTRAINT fines_carid_fkey FOREIGN KEY (carid) REFERENCES public.cars(carid);


-- Completed on 2025-11-03 22:26:01

--
-- PostgreSQL database dump complete
--

\unrestrict puNFULCfmdzyvQqAod3Q0pGIaAb9K4jATXApk4agiATOPcrL7VpTioGgQ2aa713

