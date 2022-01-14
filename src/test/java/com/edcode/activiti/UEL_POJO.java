package com.edcode.activiti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author eddie.lee
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UEL_POJO implements Serializable {

    private static final long serialVersionUID = 3288959023641785980L;

    /**
     * 执行人
     */
    private String executor;

    /**
     * 支付
     */
    private String pay;

}
