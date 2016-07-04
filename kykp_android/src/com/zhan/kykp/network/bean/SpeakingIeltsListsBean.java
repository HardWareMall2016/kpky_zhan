package com.zhan.kykp.network.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/10/16.
 */
public class SpeakingIeltsListsBean {

    /**
     * status : 1
     * message :
     * datas : [{"updatedAt":1440641955,"index":111,"QuestionType":"Society","createdAt":1440640054,"QuestionTitle":"Salaries for skilled people","Test":"4","Question":"Do you think television and films can make people want to get new possessions? Why do they have this effect?\nAre there any benefits to society of people wanting to get new possessions?\nWhy do you think this is?\nDo you think people will consider that having lots of possessions is a sign of success in the future? Why?","Book":"剑桥10","Part":"3(2)","objectId":"55dedcb660b2e052a06896c6"},{"updatedAt":1440641573,"index":110,"QuestionType":"Preference","createdAt":1440640049,"QuestionTitle":"Owning things","Test":"4","Question":"What types of things do young people in your country most want to own today?\nWhy is this?\nWhy do some people feel they need to own things?\nDo you think that owning lots of things makes people happy? Why?","Book":"剑桥10","Part":"3(1)","objectId":"55dedcb160b2e366679002ea"},{"updatedAt":1440641570,"index":109,"QuestionType":"Object","createdAt":1440640046,"QuestionTitle":"Describe something you don't have now but would really like to own in the future.","Test":"4","Question":"You should say:\nwhat this thing is\nhow long you have wanted to own it where you first saw it and \nexplain why you would like to own it.\n","Book":"剑桥10","Part":"2","objectId":"55dedcae00b0de09159f43b3"},{"updatedAt":1440641567,"index":108,"QuestionType":"Education","createdAt":1440640042,"QuestionTitle":"School","Test":"4","Question":"\u2022 Did you go to secondary/high school near to where you lived? [Why/Why not?]\n\u2022 What did you like about your secondary/high school? [Why?] \n\u2022 Tell me about anything you didn't like at your school.\n\u2022 How do you think your school could be improved? [Why/Why not?]","Book":"剑桥10","Part":"1","objectId":"55dedcaa00b0c86e3f07a393"},{"updatedAt":1440641563,"index":107,"QuestionType":"Leisure","createdAt":1440640039,"QuestionTitle":"Children's free-time activities","Test":"3","Question":"What are the most popular free-time activities with children today?\nDo you think the free-time activities children do today are good for their health? Why is that?\nHow do you think children's activities will change in the future? Will this be a positive change?","Book":"剑桥10","Part":"3(2)","objectId":"55dedca760b2719eb6792d9d"},{"updatedAt":1440641562,"index":106,"QuestionType":"Relationship","createdAt":1440640035,"QuestionTitle":"Relationships between parents and children","Test":"3","Question":"How much time do children spend with their parents in your country? Do you think that is enough?\nHow important do you think spending time together is for the relationships between parents and children? Why?\nHave relationships between parents and children changed in recent years? Why do you think that is?\n","Book":"剑桥10","Part":"3(1)","objectId":"55dedca3ddb25bb79a61acab"},{"updatedAt":1440641560,"index":105,"QuestionType":"Person","createdAt":1440640031,"QuestionTitle":"Describe a child that you know.","Test":"3","Question":"You should say:\nwho this child is and how often you see him or her \nhow old this child is \nwhat he or she is like and \nexplain what you feel about this child.\n","Book":"剑桥10","Part":"2","objectId":"55dedc9f60b2f3a99275831c"},{"updatedAt":1440641558,"index":104,"QuestionType":"Tourism","createdAt":1440640020,"QuestionTitle":"Travel","Test":"3","Question":"\u2022 Do you enjoy travelling? [Why/Why not?]\n\u2022 Have you done much travelling? [Why/Why not?]\n\u2022 Do you think it's better to travel alone or with other people? [Why?] \n\u2022 Where would you like to travel in the future? [Why?]","Book":"剑桥10","Part":"1","objectId":"55dedc9460b2f3a9927580fb"},{"updatedAt":1440641553,"index":103,"QuestionType":"Society","createdAt":1440640012,"QuestionTitle":"People and business","Test":"2","Question":"Why do some people want to start their own business?\nAre there any disadvantages to running a business? Which is the most serious?\nWhat are the most important qualities that a good business person needs? Why is that?","Book":"剑桥10","Part":"3(2)","objectId":"55dedc8c00b09b534433eaaf"},{"updatedAt":1440641549,"index":102,"QuestionType":"Lifestyle","createdAt":1440640007,"QuestionTitle":"Local business","Test":"2","Question":"What types of local business are there in your neighbourhood? Are there any restaurants, shops or dentists for example?\nDo you think local businesses are important for a neighbourhood? In what way?\nHow do large shopping malls and commercial centres affect small local businesses? Why do you think that is?","Book":"剑桥10","Part":"3(1)","objectId":"55dedc8760b271314afdaaf9"}]
     */

    private int status;
    private String message;
    private List<DatasEntity> datas;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDatas(List<DatasEntity> datas) {
        this.datas = datas;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<DatasEntity> getDatas() {
        return datas;
    }

    public static class DatasEntity {
        /**
         * updatedAt : 1440641955
         * index : 111
         * QuestionType : Society
         * createdAt : 1440640054
         * QuestionTitle : Salaries for skilled people
         * Test : 4
         * Question : Do you think television and films can make people want to get new possessions? Why do they have this effect?
         Are there any benefits to society of people wanting to get new possessions?
         Why do you think this is?
         Do you think people will consider that having lots of possessions is a sign of success in the future? Why?
         * Book : 剑桥10
         * Part : 3(2)
         * objectId : 55dedcb660b2e052a06896c6
         */

        private int updatedAt;
        private int index;
        private String QuestionType;
        private int createdAt;
        private String QuestionTitle;
        private String Test;
        private String Question;
        private String Book;
        private String Part;
        private String objectId;

        public void setUpdatedAt(int updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setQuestionType(String QuestionType) {
            this.QuestionType = QuestionType;
        }

        public void setCreatedAt(int createdAt) {
            this.createdAt = createdAt;
        }

        public void setQuestionTitle(String QuestionTitle) {
            this.QuestionTitle = QuestionTitle;
        }

        public void setTest(String Test) {
            this.Test = Test;
        }

        public void setQuestion(String Question) {
            this.Question = Question;
        }

        public void setBook(String Book) {
            this.Book = Book;
        }

        public void setPart(String Part) {
            this.Part = Part;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public int getUpdatedAt() {
            return updatedAt;
        }

        public int getIndex() {
            return index;
        }

        public String getQuestionType() {
            return QuestionType;
        }

        public int getCreatedAt() {
            return createdAt;
        }

        public String getQuestionTitle() {
            return QuestionTitle;
        }

        public String getTest() {
            return Test;
        }

        public String getQuestion() {
            return Question;
        }

        public String getBook() {
            return Book;
        }

        public String getPart() {
            return Part;
        }

        public String getObjectId() {
            return objectId;
        }
    }
}
