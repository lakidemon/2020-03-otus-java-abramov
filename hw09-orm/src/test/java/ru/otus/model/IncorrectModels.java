package ru.otus.model;

import lombok.AllArgsConstructor;
import ru.otus.jdbc.mapper.Id;

public class IncorrectModels {

    @AllArgsConstructor
    public class WithoutAnnotation {
        private int id;
        private String field1;
        private String field2;
    }

    @AllArgsConstructor
    public class WithWrongIdTypeAnnotation {
        private double id;
        private String field1;
        private String field2;
    }

    public class WithoutConstructor {
        @Id
        private int id;
        private String field1;
        private String field2;
    }

    public class WithoutRequiredConstructor {
        @Id
        private int id;
        private String field1;
        private String field2;

        public WithoutRequiredConstructor(int id, Number wtf, String field1) {
        }
    }

}