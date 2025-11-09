--
-- PostgreSQL database dump
--

\restrict LYcbT1FCK6XWMrdvxW9qf337eOopKDJTBqTpOVZ7guN5pECiyDMyvUSBAPxC2zm

-- Dumped from database version 18.0
-- Dumped by pg_dump version 18.0

-- Started on 2025-11-07 22:24:10

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
-- TOC entry 219 (class 1259 OID 24879)
-- Name: carbrands; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.carbrands (
    idbrand bigint NOT NULL,
    name character varying(30) NOT NULL
);


ALTER TABLE public.carbrands OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 24884)
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
-- Dependencies: 220
-- Name: carbrands_idbrand_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.carbrands_idbrand_seq OWNED BY public.carbrands.idbrand;


--
-- TOC entry 221 (class 1259 OID 24885)
-- Name: carmodels; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.carmodels (
    modelid bigint NOT NULL,
    name character varying(30) NOT NULL,
    carbrand bigint NOT NULL
);


ALTER TABLE public.carmodels OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 24891)
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
-- Dependencies: 222
-- Name: carmodels_modelid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.carmodels_modelid_seq OWNED BY public.carmodels.modelid;


--
-- TOC entry 223 (class 1259 OID 24892)
-- Name: cars; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cars (
    carid bigint NOT NULL,
    carbrand bigint NOT NULL,
    carmodel bigint NOT NULL,
    "licensePlate" character varying(20) NOT NULL,
    owner character varying(100) NOT NULL,
    address character varying(100) NOT NULL,
    colour character varying NOT NULL
);


ALTER TABLE public.cars OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 24904)
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
-- Dependencies: 224
-- Name: cars_carid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.cars_carid_seq OWNED BY public.cars.carid;


--
-- TOC entry 225 (class 1259 OID 24905)
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
-- TOC entry 226 (class 1259 OID 24917)
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
-- Dependencies: 226
-- Name: fines_fineid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.fines_fineid_seq OWNED BY public.fines.fineid;


--
-- TOC entry 4770 (class 2604 OID 24918)
-- Name: carbrands idbrand; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carbrands ALTER COLUMN idbrand SET DEFAULT nextval('public.carbrands_idbrand_seq'::regclass);


--
-- TOC entry 4771 (class 2604 OID 24919)
-- Name: carmodels modelid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carmodels ALTER COLUMN modelid SET DEFAULT nextval('public.carmodels_modelid_seq'::regclass);


--
-- TOC entry 4772 (class 2604 OID 24920)
-- Name: cars carid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars ALTER COLUMN carid SET DEFAULT nextval('public.cars_carid_seq'::regclass);


--
-- TOC entry 4773 (class 2604 OID 24921)
-- Name: fines fineid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fines ALTER COLUMN fineid SET DEFAULT nextval('public.fines_fineid_seq'::regclass);


--
-- TOC entry 4942 (class 0 OID 24879)
-- Dependencies: 219
-- Data for Name: carbrands; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.carbrands VALUES (1, 'Volkswagen');
INSERT INTO public.carbrands VALUES (2, 'Audi');
INSERT INTO public.carbrands VALUES (3, 'Fiat');
INSERT INTO public.carbrands VALUES (4, 'Renault');
INSERT INTO public.carbrands VALUES (5, 'Chevrolet');
INSERT INTO public.carbrands VALUES (6, 'Ford');
INSERT INTO public.carbrands VALUES (7, 'Nissan');


--
-- TOC entry 4944 (class 0 OID 24885)
-- Dependencies: 221
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
INSERT INTO public.carmodels VALUES (13, 'Corsa', 5);
INSERT INTO public.carmodels VALUES (14, 'Astra', 5);
INSERT INTO public.carmodels VALUES (15, 'Aveo', 5);
INSERT INTO public.carmodels VALUES (16, 'Prisma', 5);
INSERT INTO public.carmodels VALUES (17, 'Meriva', 5);
INSERT INTO public.carmodels VALUES (18, 'TT', 2);
INSERT INTO public.carmodels VALUES (19, 'RS5', 2);
INSERT INTO public.carmodels VALUES (20, 'A5', 2);
INSERT INTO public.carmodels VALUES (21, 'A6', 2);
INSERT INTO public.carmodels VALUES (22, 'R8', 2);
INSERT INTO public.carmodels VALUES (23, 'R7', 2);
INSERT INTO public.carmodels VALUES (24, 'R6', 2);
INSERT INTO public.carmodels VALUES (25, '500', 3);
INSERT INTO public.carmodels VALUES (26, '600', 3);
INSERT INTO public.carmodels VALUES (27, 'Duna', 3);
INSERT INTO public.carmodels VALUES (28, 'Uno', 3);
INSERT INTO public.carmodels VALUES (29, 'Punto', 3);
INSERT INTO public.carmodels VALUES (30, 'Siena', 3);
INSERT INTO public.carmodels VALUES (31, 'Mobi', 3);
INSERT INTO public.carmodels VALUES (32, 'Kicks', 7);
INSERT INTO public.carmodels VALUES (33, 'Sentra', 7);
INSERT INTO public.carmodels VALUES (34, 'Versa', 7);
INSERT INTO public.carmodels VALUES (35, 'March', 7);
INSERT INTO public.carmodels VALUES (36, 'Silvia', 7);
INSERT INTO public.carmodels VALUES (37, 'GTR', 7);
INSERT INTO public.carmodels VALUES (38, 'R12', 4);
INSERT INTO public.carmodels VALUES (39, 'R18', 4);
INSERT INTO public.carmodels VALUES (40, 'Twingo', 4);
INSERT INTO public.carmodels VALUES (41, 'Megane', 4);
INSERT INTO public.carmodels VALUES (42, 'Fuego', 4);
INSERT INTO public.carmodels VALUES (43, 'Kangoo', 4);
INSERT INTO public.carmodels VALUES (44, 'Fluence', 4);
INSERT INTO public.carmodels VALUES (45, 'Oroch', 7);
INSERT INTO public.carmodels VALUES (46, 'Golf', 1);
INSERT INTO public.carmodels VALUES (47, 'Gol Trend', 1);
INSERT INTO public.carmodels VALUES (48, 'Scirocco', 1);
INSERT INTO public.carmodels VALUES (49, 'Tiguan', 1);
INSERT INTO public.carmodels VALUES (50, 'T-Cross', 1);
INSERT INTO public.carmodels VALUES (51, 'Amarok', 1);
INSERT INTO public.carmodels VALUES (52, 'Bora', 1);
INSERT INTO public.carmodels VALUES (53, 'Passat', 1);
INSERT INTO public.carmodels VALUES (54, 'Fox', 1);
INSERT INTO public.carmodels VALUES (55, 'Suran', 1);


--
-- TOC entry 4946 (class 0 OID 24892)
-- Dependencies: 223
-- Data for Name: cars; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.cars VALUES (16, 3, 26, 'UHD453', 'Kevin Lopez', 'Cordoba 2489', 'Silver');
INSERT INTO public.cars VALUES (1, 1, 1, 'AB123CD', 'Juan Perez', 'Av Siempreviva 742', 'Red');
INSERT INTO public.cars VALUES (2, 1, 2, 'CD456EF', 'Lucia Gomez', 'Belgrano 123', 'Black');
INSERT INTO public.cars VALUES (3, 2, 3, 'EF789GH', 'Martin Ruiz', 'Mitre 890', 'Grey');
INSERT INTO public.cars VALUES (4, 3, 5, 'GH321IJ', 'Ana Torres', 'San Martin 77', 'White');
INSERT INTO public.cars VALUES (5, 5, 9, 'IJ654KL', 'Pedro Rios', 'Córdoba 555', 'Blue');
INSERT INTO public.cars VALUES (6, 6, 11, 'KL987MN', 'Maria Lopez', 'Rivadavia 400', 'Red');
INSERT INTO public.cars VALUES (7, 1, 1, 'AB123CE', 'Juan Perez', 'Av Siempreviva 742', 'Red');
INSERT INTO public.cars VALUES (8, 1, 2, 'CD456ED', 'Lucia Gomez', 'Belgrano 123', 'Black');
INSERT INTO public.cars VALUES (9, 2, 3, 'EF789GG', 'Martin Ruiz', 'Mitre 890', 'Grey');
INSERT INTO public.cars VALUES (10, 3, 5, 'GH321IK', 'Ana Torres', 'San Martin 77', 'White');
INSERT INTO public.cars VALUES (11, 5, 9, 'IJ654KP', 'Pedro Rios', 'Córdoba 555', 'Blue');
INSERT INTO public.cars VALUES (12, 6, 11, 'KL987MM', 'Maria Lopez', 'Rivadavia 400', 'Red');
INSERT INTO public.cars VALUES (13, 6, 12, 'RR132GF', 'Miguel Rodriguez', 'Cerrito 1250', 'Black');
INSERT INTO public.cars VALUES (17, 1, 46, 'AD123AA', 'Carlos Sanchez', 'Colon 1500', 'Red');
INSERT INTO public.cars VALUES (18, 1, 47, 'AD123AB', 'Laura Torres', 'Alberti 2100', 'Blue');
INSERT INTO public.cars VALUES (19, 1, 48, 'AD123AC', 'Marcos Gimenez', 'Guemes 3030', 'Black');
INSERT INTO public.cars VALUES (20, 2, 18, 'AD123AD', 'Sofia Castro', 'Paso 4500', 'White');
INSERT INTO public.cars VALUES (21, 2, 19, 'AD123AE', 'Diego Fernandez', 'Matheu 100', 'Silver');
INSERT INTO public.cars VALUES (22, 3, 25, 'AD123AF', 'Valentina Ruiz', 'Castelli 980', 'Green');
INSERT INTO public.cars VALUES (23, 3, 26, 'AD123AG', 'Javier Diaz', 'Garay 1300', 'Red');
INSERT INTO public.cars VALUES (24, 4, 38, 'AD123AH', 'Camila Acosta', 'Moreno 2200', 'Blue');
INSERT INTO public.cars VALUES (25, 4, 39, 'AD123AI', 'Matias Romero', 'Falucho 1900', 'Grey');
INSERT INTO public.cars VALUES (26, 5, 13, 'AD123AJ', 'Agustina Sosa', 'Brown 2550', 'White');
INSERT INTO public.cars VALUES (27, 5, 14, 'AD123AK', 'Nicolas Vazquez', 'Formosa 700', 'Black');
INSERT INTO public.cars VALUES (28, 6, 11, 'AD123AL', 'Julieta Benitez', 'Chaco 1150', 'Red');
INSERT INTO public.cars VALUES (29, 7, 32, 'AD123AM', 'Facundo Herrera', 'La Rioja 2020', 'Blue');
INSERT INTO public.cars VALUES (30, 7, 33, 'AD123AN', 'Luciana Suarez', 'Catamarca 2300', 'Silver');
INSERT INTO public.cars VALUES (31, 1, 49, 'AD123AO', 'Hernan Molina', 'Salta 2100', 'Green');
INSERT INTO public.cars VALUES (32, 1, 50, 'AE234BA', 'Paula Chavez', 'Jujuy 1850', 'Black');
INSERT INTO public.cars VALUES (33, 2, 20, 'AE234BB', 'Esteban Paredes', 'Bolivar 2800', 'White');
INSERT INTO public.cars VALUES (34, 3, 27, 'AE234BC', 'Carolina Ponce', 'Roca 1400', 'Red');
INSERT INTO public.cars VALUES (35, 4, 40, 'AE234BD', 'Gonzalo Vega', 'Olavarria 2900', 'Blue');
INSERT INTO public.cars VALUES (36, 5, 15, 'AE234BE', 'Victoria Flores', 'Gascón 800', 'Grey');
INSERT INTO public.cars VALUES (37, 7, 34, 'AE234BF', 'Ramiro Campos', 'Saavedra 3100', 'White');
INSERT INTO public.cars VALUES (38, 1, 51, 'AE234BG', 'Florencia Nuñez', 'Peña 400', 'Black');
INSERT INTO public.cars VALUES (39, 1, 52, 'AE234BH', 'Bruno Ortiz', 'Tucuman 2600', 'Silver');
INSERT INTO public.cars VALUES (40, 2, 21, 'AE234BI', 'Micaela Rios', 'Buenos Aires 3300', 'Blue');
INSERT INTO public.cars VALUES (41, 3, 28, 'AE234BJ', 'Sebastian Alvarez', 'Alvarado 1700', 'Red');
INSERT INTO public.cars VALUES (42, 4, 41, 'AE234BK', 'Elias Galarza', 'Avellaneda 1200', 'Green');
INSERT INTO public.cars VALUES (43, 5, 16, 'AE234BL', 'Martina Medina', 'Santiago del Estero 2400', 'White');
INSERT INTO public.cars VALUES (44, 7, 35, 'AE234BM', 'Tomas Guzman', 'Santa Fe 2000', 'Black');
INSERT INTO public.cars VALUES (45, 1, 53, 'BAA400', 'Renata Quiroga', 'Corrientes 2150', 'Grey');
INSERT INTO public.cars VALUES (46, 2, 22, 'BAA401', 'Dario Franco', 'Entre Rios 1990', 'Red');
INSERT INTO public.cars VALUES (47, 3, 29, 'BAA402', 'Emilia Pardo', 'San Luis 3000', 'Blue');
INSERT INTO public.cars VALUES (48, 4, 42, 'BAA403', 'Valentin Cortes', 'Funes 3500', 'Silver');
INSERT INTO public.cars VALUES (49, 5, 17, 'BAA404', 'Catalina Bravo', 'Jara 1800', 'White');
INSERT INTO public.cars VALUES (50, 7, 36, 'BAA405', 'Mariano Luna', 'Dorrego 750', 'Green');
INSERT INTO public.cars VALUES (51, 1, 54, 'BAA406', 'Josefina Soler', 'Talbot 900', 'Black');


--
-- TOC entry 4948 (class 0 OID 24905)
-- Dependencies: 225
-- Data for Name: fines; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.fines VALUES (1, '2025-11-07 11:42:59.23598-03', 'PARKING', 30000.00, 2, 'Parking Camera 2', 'FinesPhoto6.jpg', '000001000003000000', 28);
INSERT INTO public.fines VALUES (2, '2025-11-07 11:43:31.95605-03', 'SPEEDING', 57500.00, 4, 'Radar 3', 'FinesPhoto6.jpg', '000002000005750000', 5);
INSERT INTO public.fines VALUES (3, '2025-11-07 22:13:31.18941-03', 'RED_LIGHT', 80000.00, 5, 'Semaphore 3', 'FinesPhoto6.jpg', '000003000008000000', 38);
INSERT INTO public.fines VALUES (4, '2025-11-07 22:13:50.479793-03', 'RED_LIGHT', 80000.00, 5, 'Semaphore 30', 'FinesPhoto11.jpeg', '000004000008000000', 1);


--
-- TOC entry 4959 (class 0 OID 0)
-- Dependencies: 220
-- Name: carbrands_idbrand_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.carbrands_idbrand_seq', 6, true);


--
-- TOC entry 4960 (class 0 OID 0)
-- Dependencies: 222
-- Name: carmodels_modelid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.carmodels_modelid_seq', 12, true);


--
-- TOC entry 4961 (class 0 OID 0)
-- Dependencies: 224
-- Name: cars_carid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.cars_carid_seq', 51, true);


--
-- TOC entry 4962 (class 0 OID 0)
-- Dependencies: 226
-- Name: fines_fineid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.fines_fineid_seq', 4, true);


--
-- TOC entry 4776 (class 2606 OID 24923)
-- Name: carbrands carbrands_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carbrands
    ADD CONSTRAINT carbrands_pkey PRIMARY KEY (idbrand);


--
-- TOC entry 4778 (class 2606 OID 24925)
-- Name: carmodels carmodels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carmodels
    ADD CONSTRAINT carmodels_pkey PRIMARY KEY (modelid);


--
-- TOC entry 4781 (class 2606 OID 24927)
-- Name: cars cars_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT cars_pkey PRIMARY KEY (carid);


--
-- TOC entry 4787 (class 2606 OID 24929)
-- Name: fines fines_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fines
    ADD CONSTRAINT fines_pkey PRIMARY KEY (fineid);


--
-- TOC entry 4785 (class 2606 OID 24931)
-- Name: cars uq_cars_licenseplate; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT uq_cars_licenseplate UNIQUE ("licensePlate");


--
-- TOC entry 4782 (class 1259 OID 24932)
-- Name: idx_cars_brand; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cars_brand ON public.cars USING btree (carbrand);


--
-- TOC entry 4783 (class 1259 OID 24933)
-- Name: idx_cars_model; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cars_model ON public.cars USING btree (carmodel);


--
-- TOC entry 4788 (class 1259 OID 24934)
-- Name: idx_fines_carid; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_fines_carid ON public.fines USING btree (carid);


--
-- TOC entry 4789 (class 1259 OID 24935)
-- Name: idx_fines_date; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_fines_date ON public.fines USING btree (finedate);


--
-- TOC entry 4790 (class 1259 OID 24936)
-- Name: idx_fines_type; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_fines_type ON public.fines USING btree (type);


--
-- TOC entry 4779 (class 1259 OID 24937)
-- Name: idx_models_brand; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_models_brand ON public.carmodels USING btree (carbrand);


--
-- TOC entry 4791 (class 2606 OID 24938)
-- Name: carmodels carmodels_carbrand_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carmodels
    ADD CONSTRAINT carmodels_carbrand_fkey FOREIGN KEY (carbrand) REFERENCES public.carbrands(idbrand);


--
-- TOC entry 4792 (class 2606 OID 24943)
-- Name: cars cars_carbrand_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT cars_carbrand_fkey FOREIGN KEY (carbrand) REFERENCES public.carbrands(idbrand);


--
-- TOC entry 4793 (class 2606 OID 24948)
-- Name: cars cars_carmodel_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT cars_carmodel_fkey FOREIGN KEY (carmodel) REFERENCES public.carmodels(modelid);


--
-- TOC entry 4794 (class 2606 OID 24953)
-- Name: fines fines_carid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fines
    ADD CONSTRAINT fines_carid_fkey FOREIGN KEY (carid) REFERENCES public.cars(carid);


-- Completed on 2025-11-07 22:24:11

--
-- PostgreSQL database dump complete
--

\unrestrict LYcbT1FCK6XWMrdvxW9qf337eOopKDJTBqTpOVZ7guN5pECiyDMyvUSBAPxC2zm

