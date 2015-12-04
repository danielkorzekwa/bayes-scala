package dk.bayes.factorgraph2.api

trait SingleFactor[V1] {

  private val msgV1 = Message[V1]

  //init
  {
    msgV1.set(getInitialMsgV1())
    getV1().addMessage(msgV1)
  }

  def getMsgV1(): Message[V1] = msgV1
  def updateMsgV1() = msgV1.set(calcNewMsgV1())

  /**
   * Definitions
   */

  def getV1(): Variable[V1]
  def getInitialMsgV1(): V1
  def calcNewMsgV1(): V1

}