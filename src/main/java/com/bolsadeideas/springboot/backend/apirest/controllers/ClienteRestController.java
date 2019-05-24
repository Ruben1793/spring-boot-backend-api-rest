package com.bolsadeideas.springboot.backend.apirest.controllers;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.services.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {

    @Autowired
    private IClienteService clienteService;

    @GetMapping("/clientes")
    public List<Cliente> index(){
        return clienteService.findAll();
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<?> show(@PathVariable Long id){
        Cliente cliente;
        Map<String, Object> response = new HashMap<>();
        try{
            cliente = clienteService.findById(id);

        } catch(DataAccessException e){
            response.put("mensaje", "Error al realizar consulta en base de datos!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (cliente == null){
            response.put("mensaje", "El cliente ID: ".concat(" no existe en base de datos!"));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result){
        Cliente clienteNew;
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()){
            /*
            List<String> errors = new ArrayList<>();
            for (FieldError error: result.getFieldErrors()) {
                errors.add("El campo '" + error.getField() + "' " + error.getDefaultMessage());
            }
            */
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(error -> "El campo '" + error.getField() + "' " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try{
            clienteNew = clienteService.save(cliente);
        } catch(DataAccessException e){
            response.put("mensaje", "Error al realizar consulta en base de datos!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(clienteNew, HttpStatus.CREATED);
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result ,@PathVariable Long id){
        Cliente clienteActual = clienteService.findById(id);
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()){
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(error -> "El campo '" + error.getField() + "' " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Cliente clienteUpdated = null;
        if (clienteActual == null){
            response.put("mensaje", "Error: no pudo editar, el cliente ID: ".concat(" no existe en base de datos"));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        clienteActual.setNombre(cliente.getNombre());
        clienteActual.setApellido(cliente.getApellido());
        clienteActual.setEmail(cliente.getEmail());
        try{
            clienteUpdated = clienteService.save(clienteActual);
        } catch(DataAccessException e){
            response.put("mensaje", "Error al actualizar el cliente en la base de datos!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(clienteUpdated, HttpStatus.CREATED);
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            clienteService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el cliente en la base de datos!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "lEl cliente ha sido eliminado con exito!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
