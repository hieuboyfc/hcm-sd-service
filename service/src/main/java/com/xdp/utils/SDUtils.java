package com.xdp.utils;

import java.util.List;

public class SDUtils {

    private SDUtils() {
    }

    public static final String FORMULA_SETUP = "formula_setup";
    public static final String FUNC_VARIABLE = "func_variable";
    public static final String RISK_LEAVE_WORK = "risk_leave_work";
    public static final String AFFECT_LEAVE_WORK = "affect_leave_work";
    public static final String REASON_LEAVE = "reason_leave";
    public static final String FUTURE_LEADER = "future_leader";
    public static final String HEIR_SETUP = "heir_setup";
    public static final String CAREER_PATH = "career_path";
    public static final String CAREER_PATH_FLOW = "career_path_flow";
    public static final String CAREER_PATH_FLOW_EMP = "career_path_flow_emp";
    public static final String CAREER_PATH_FLOW_QUALIFIED = "career_path_flow_qualified";
    public static final String CAREER_PATH_SETUP = "career_path_setup";

    public static final String RATE_EE = "Rate_ee";
    public static final String RATE_POS = "Rate_pos";
    public static final String WEIGHT_POS = "Weight_pos";
    public static final String LEVEL_EE = "Level_ee";
    public static final String LEVEL_POS = "Level_pos";
    public static final String COM_POS = "Com_pos";
    public static final String CHECK_CONTRACT = "checkContract";
    public static final String CHECK_ACTION = "checkAction";

    public static class NameMethod {
        private NameMethod() {
        }

        public static final String SEARCH = "search";
        public static final String CREATE = "create";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String GET_DETAIL = "getDetail";
        public static final String REFRESH = "refresh";
    }

    public static class LanguageMessage {
        private LanguageMessage() {
        }

        public static final String REQUIRED_FIELD = "required-field-null";

        public static class CareerPath {
            private CareerPath() {
            }

            public static final String NOT_FOUND = "sd-career-path-not-found";
            public static final String EXIST = "sd-career-path-exist";
            public static final String CODE_NOT_CHANGE = "sd-career-path-code-not-changed";
            public static final String IN_FLOW_NOT_FOUND = "sd-career-path-in-flow-not-found";
            public static final String IN_FLOW_POSITION_CODE_NOT_FOUND = "sd-career-path-in-flow-position-code-not-found";
        }

        public static class CareerPathFlow {
            private CareerPathFlow() {
            }

            public static final String NOT_FOUND = "sd-career-path-flow-not-found";
            public static final String CODE_EXCEED_THE_CHARACTER = "sd-career-path-flow-code-exceed-the-character";
            public static final String POSITION_EXIST = "sd-career-path-flow-position-exist";
        }
    }

    public static class State {
        private State() {
        }

        public static class Employee {
            private Employee() {
            }

            public static final String OFFICIAL = "official";
            public static final String CTV = "ctv";
            public static final String WORKING = "working";
            public static final String TRAINEE = "trainee";
            public static final String UNPAID = "unpaid";
            public static final String QUIT = "quit";
            public static final String MATERNITY = "maternity";
            public static final String INTERN = "intern";
            public static final String PROBATION = "probation";
            public static final String CANDIDATE = "candidate";
            public static final List<String> DEFAULT = List.of(OFFICIAL, CTV, WORKING, TRAINEE, UNPAID, QUIT, MATERNITY, INTERN, PROBATION, CANDIDATE);
        }
    }

    public static class Symbol {
        private Symbol() {
        }

        public static final String COMMA = ",";
        public static final String ASTERISK = "*";
        public static final String SPACE = " ";
        public static final String UNDERSCORE = "_";
        public static final String QUESTION_MARK = "?";
        public static final String SLASH = "/";
        public static final String SHARP = "#";
        public static final String NULL = "NULL";
        public static final String EMPTY = "";
        public static final String DOT = ".";
        public static final String PERCENT = "%";
        public static final String PERCENT_X2 = "%%";
    }

    public static class Number {
        private Number() {
        }

        public static final int N0 = 0;
        public static final int N1 = 1;
        public static final int N2 = 2;
        public static final int N3 = 3;
        public static final int N4 = 4;
        public static final int N5 = 5;
        public static final int N6 = 6;
        public static final int N7 = 7;
        public static final int N8 = 8;
        public static final int N9 = 9;
    }

    public static class Code {
        private Code() {
        }

        public static final String N5000 = "5000";
    }

}
