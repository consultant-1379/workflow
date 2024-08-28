/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.camunda.eric.orion.ocwf.entity;

public class CheckOrderStatusResponseEntity {

    private OrderStatus status;

    private OrderData data;

    /**
     * @return the status
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(final OrderStatus status) {
        this.status = status;
    }

    /**
     * @return the data
     */
    public OrderData getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(final OrderData data) {
        this.data = data;
    }

}
