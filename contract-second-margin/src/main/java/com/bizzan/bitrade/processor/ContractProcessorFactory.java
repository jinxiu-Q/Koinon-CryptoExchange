package com.bizzan.bitrade.processor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ContractProcessorFactory {

    private ConcurrentHashMap<String, ContractProcessor> processorMap;

    public ContractProcessorFactory() {
        processorMap = new ConcurrentHashMap<>();
    }

    public void addProcessor(String symbol, ContractProcessor processor) {
        log.info("ContractProcessorFactory addProcessor = {}" + symbol);
        processorMap.put(symbol, processor);
    }

    public boolean containsProcessor(String symbol) {
        return processorMap != null && processorMap.containsKey(symbol);
    }

    public ContractProcessor getProcessor(String symbol) {
        return processorMap.get(symbol);
    }

    public ConcurrentHashMap<String, ContractProcessor> getProcessorMap() {
        return processorMap;
    }
}
