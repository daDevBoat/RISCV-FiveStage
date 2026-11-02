package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.{IntParam, MultiIOModule, dontTouch}

class BTBEntry extends Bundle {
  val valid  = Bool()
  val target = UInt(32.W)
}

class BranchPredictor extends MultiIOModule {
  val io = IO(
    new Bundle {
      // Reading prediction
      val PC = Input(UInt(32.W))
      val branchPredictedTaken = Output(Bool())
      val predictedTarget = Output(UInt(32.W))
      val BTBHit = Output(Bool())

      // Updating prediciton
      val updateFlag = Input(Bool())
      val branchWasTaken = Input(Bool())
      val PCOfBranchTaken = Input(UInt(32.W))
      val branchTarget = Input(UInt(32.W))
    }
  )

  val NUM_ENTRIES = 64

  val counters = RegInit(VecInit(Seq.fill(NUM_ENTRIES)(1.U(2.W))))
  val BTB = RegInit(VecInit(Seq.fill(NUM_ENTRIES)(0.U.asTypeOf(new BTBEntry))))

  val index = io.PC(7, 2)

  when(counters(index) > 1.U) {
    io.branchPredictedTaken := true.B
  }.otherwise {
    io.branchPredictedTaken := false.B
  }

  when(io.branchPredictedTaken && BTB(index).valid) {
    io.predictedTarget := BTB(index).target
    io.BTBHit := true.B
  }.otherwise {
    io.predictedTarget := 0.U
    io.BTBHit := false.B
  }

  when(io.updateFlag) {
    val index = io.PCOfBranchTaken(7, 2)
    when(io.branchWasTaken) {
      BTB(index).target := io.branchTarget
      BTB(index).valid := true.B
      when(counters(index) =/= 3.U) {
        counters(index) := counters(index) + 1.U
      }
    }.otherwise {
      when(counters(index) =/= 0.U) {
        counters(index) := counters(index) - 1.U
      }
    }
  }



}