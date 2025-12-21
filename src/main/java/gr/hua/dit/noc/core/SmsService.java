package gr.hua.dit.noc.core;

import gr.hua.dit.noc.core.model.SendSmsRequest;
import gr.hua.dit.noc.core.model.SendSmsResult;

public interface SmsService {
   SendSmsResult send(SendSmsRequest request);
}
