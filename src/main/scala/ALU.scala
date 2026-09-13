package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule

class ALU extends MultiIOModule {
  val io = IO(
    new Bundle {
      val in1 = Input(UInt(32.W))
      val in2 = Input(UInt(32.W))
      val aluOp = Input(UInt(32.W))
      val aluResult = Output(UInt(32.W))
      val zeroFlag = Output(Bool())
    }
  )

  // Defining ALU local op codes
  ALUOps

  val ALUopMap = Seq(
    ALUOps.ADD    -> (io.in1 + io.in2),
    ALUOps.SUB    -> (io.in1 - io.in2),
    ALUOps.AND    -> (io.in1 & io.in2),
    ALUOps.OR     -> (io.in1 | io.in2),
    ALUOps.XOR    -> (io.in1 ^ io.in2),
    ALUOps.SLT    -> (io.in1.asSInt < io.in2.asSInt),
    ALUOps.SLL    -> (io.in1 << io.in2(4, 0)),
    ALUOps.SLTU   -> (io.in1 < io.in2),
    ALUOps.SRL    -> (io.in1 >> io.in2(4, 0)),
    ALUOps.SRA    -> (io.in1.asSInt >> io.in2(4, 0)).asUInt,
    ALUOps.COPY_A -> io.in1,
    ALUOps.COPY_B -> io.in2
  )

  io.aluResult := MuxLookup(io.aluOp, 0.U(32.W), ALUopMap)

  // Sets zero flag
  io.zeroFlag := io.aluResult === 0.U


}
