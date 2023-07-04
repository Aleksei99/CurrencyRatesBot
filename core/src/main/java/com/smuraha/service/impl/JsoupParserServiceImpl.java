package com.smuraha.service.impl;

import com.smuraha.model.Bank;
import com.smuraha.model.CurrencyRate;
import com.smuraha.model.CurrencyUpdateHistory;
import com.smuraha.model.enums.Currencies;
import com.smuraha.repository.BankRepo;
import com.smuraha.repository.CurrencyUpdateHistoryRepo;
import com.smuraha.service.JsoupParserService;
import com.smuraha.service.dto.BankCurrencyDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JsoupParserServiceImpl implements JsoupParserService {

    private final BankRepo bankRepo;
    private final CurrencyUpdateHistoryRepo historyRepo;

    @Override
    public void parseAndUpdate(String url) throws IOException {

        if (historyRepo.findByUpdateTimeIsGreaterThanEqual(LocalDateTime.now().minusHours(1)).isEmpty()) {

            historyRepo.save(new CurrencyUpdateHistory(LocalDateTime.now()));
            Map<String, List<BankCurrencyDto>> bankCurrencyInfo = getBankCurrencyInfo(url);
            Set<Map.Entry<String, List<BankCurrencyDto>>> entries = bankCurrencyInfo.entrySet();

            for (Map.Entry<String, List<BankCurrencyDto>> entry : entries) {
                String bankName = entry.getKey();
                Bank bank = bankRepo.findBankByBankName(bankName);
                if (bank == null) {
                    bank = bankRepo.saveAndFlush(Bank.builder().bankName(bankName).build());
                }
                List<CurrencyRate> rates = getCurrencyRatesOfDto(entry.getValue(), bank);
                bank.setRates(rates);
                bankRepo.saveAndFlush(bank);
            }
        }
    }

    private List<CurrencyRate> getCurrencyRatesOfDto(List<BankCurrencyDto> dtos, Bank bank) {
        List<CurrencyRate> rates = new ArrayList<>();
        for (BankCurrencyDto dto : dtos) {
            rates.add(CurrencyRate.builder()
                    .currency(dto.getCurrency())
                    .rateBuy(dto.getRateBuy())
                    .rateSell(dto.getRateSell())
                    .lastUpdate(LocalDateTime.now())
                    .bank(bank)
                    .build());
        }
        return rates;
    }

    private Map<String, List<BankCurrencyDto>> getBankCurrencyInfo(String url) throws IOException {
        Connection connection = Jsoup.connect(url);
        Document document = connection.get();
        Map<String, List<BankCurrencyDto>> result = new HashMap<>();
        Elements bankRows = document.getElementsByAttributeValueContaining("class", "currencies-courses__row-main");
        for (Element bankRow : bankRows) {
            List<BankCurrencyDto> bankCurrencyList = new ArrayList<>();
            String bankName = bankRow.getElementsByAttribute("alt").attr("alt");
            Elements currencyCells = bankRow.getElementsByAttributeValueContaining("class", "currencies-courses__currency-cell");
            int count = 0;
            int currencyOrdinal = 0;
            BigDecimal bankSell = new BigDecimal(0);
            BigDecimal bankBuy = new BigDecimal(0);
            for (Element currencyCell : currencyCells) {
                if (currencyCell.childNodes().size() > 0) {
                    Node node = currencyCell.childNode(0);
                    String rateString = node.childNode(0).outerHtml();
                    if (count % 2 == 0) {
                        bankBuy = new BigDecimal(rateString);
                    } else {
                        bankSell = new BigDecimal(rateString);
                    }
                    if (count % 2 != 0 && bankBuy.compareTo(bankSell) > 0) {
                        BigDecimal cup;
                        cup = bankBuy;
                        bankBuy = bankSell;
                        bankSell = cup;
                    }
                    count++;
                    if (count % 2 == 0) {
                        bankCurrencyList.add(new BankCurrencyDto(Currencies.values()[currencyOrdinal], bankBuy, bankSell));
                        currencyOrdinal++;
                    }
                }
            }
            result.put(bankName.toUpperCase(), bankCurrencyList);
        }
        return result;
    }
}
