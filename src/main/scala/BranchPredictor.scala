package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.{IntParam, MultiIOModule, dontTouch}

class BTBEntry extends Bundle {
  val valid  = Bool()
  val tag    = UInt(24.W)
  val target = UInt(32.W)
}

class BranchPredictor extends MultiIOModule {
  val io = IO(
    new Bundle {
      // Reading prediction
      val PC = Input(UInt(32.W))
      val branchPredictedTaken = Output(Bool())

      // Updating prediciton
      val updateFlag = Input(Bool())
      val branchWasTaken = Input(Bool())
    }
  )

  val NUM_ENTRIES = 64

  val counters = RegInit(
    VecInit(Seq.fill(NUM_ENTRIES)(1.U(2.W)))
  )

  val index = io.PC(7, 2)

  when(counters(index) > 1.U) {
    io.branchPredictedTaken := true.B
  }.otherwise {
    io.branchPredictedTaken := false.B
  }

  when(io.updateFlag) {
    when(io.branchWasTaken) {
      counters(index) += 1.U
    }.otherwise {
      counters(index) -= 1.U
    }
  }



}