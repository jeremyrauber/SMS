package com.projetos.megabit.testes;

/**
 * Created by jeremy on 10/04/2017.
 */

public class Aluno {

    private String nome;
    private int id;
    private String serie;
    private String turno;
    private String turma;
    private String celular;
    private int ativo;
    private int value;
    private int numerochamada;


    public Aluno(int id, String nome, String serie, String turno, String turma, String celular, int value, int ativo, int numerochamada) {

        this.id = id;
        this.nome = nome;
        this.serie = serie;
        this.turno = turno;
        this.turma = turma;
        this.celular = celular;
        this.ativo = ativo;
        this.value = value;
        this.numerochamada = numerochamada;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        if(nome!= null)
            return nome;
        else
        return "Problema ao carregar Nome Aluno";
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getAtivo() {
        return ativo;
    }

    public void setAtivo(int ativo) {
        this.ativo = ativo;
    }

    public int getNumerochamada() {
        return numerochamada;
    }

    public void setNumerochamada(int numeroChamada) {
        this.numerochamada = numerochamada;
    }

    @Override
    public String toString() {
        String matriculado;
        String nome;
        if (this.getAtivo()==1)
            matriculado = "Ativo";
        else
            matriculado = "Inativo";

        if (this.getNome().length()>25)
            nome = this.getNome().substring(0,25);
        else
            nome = this.getNome();

        return this.getNumerochamada()+" - "+this.getSerie()+this.getTurma()+" - "+nome+" - "+this.getCelular()+" - "+matriculado;
    }
}