package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class InstructionDecode extends MultiIOModule {

  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val registerSetup = Input(new RegisterSetupSignals)
      val registerPeek  = Output(UInt(32.W))

      val testUpdates   = Output(new RegisterUpdates)
    })


  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction())
      val PCIn = Input(UInt(32.W))

      val instructionOut = Output(new Instruction())
      val PCOut = Output(UInt(32.W))
      val controlSignals = Output(new ControlSignals())
      val branchType = Output(UInt(3.W))
      val op1Select = Output(UInt(1.W))
      val op2Select = Output(UInt(1.W))
      val immType = Output(UInt(3.W))
      val ALUop = Output(UInt(4.W))

      val registerData1 = Output(UInt(32.W))
      val registerData2 = Output(UInt(32.W))
    }
  )

  val registers = Module(new Registers)
  val decoder   = Module(new Decoder).io


  /**
    * Setup. You should not change this code
    */
  registers.testHarness.setup := testHarness.registerSetup
  testHarness.registerPeek    := registers.io.readData1
  testHarness.testUpdates     := registers.testHarness.testUpdates

  ///*
  registers.io.readAddress1 := io.instructionIn.registerRs1
  registers.io.readAddress2 := io.instructionIn.registerRs2
  registers.io.writeEnable  := false.B
  registers.io.writeAddress := 0.U
  registers.io.writeData    := 0.U

  io.registerData1 := registers.io.readData1
  io.registerData2 := registers.io.readData2

  decoder.instruction := io.instructionIn

  io.controlSignals := decoder.controlSignals
  io.branchType := decoder.branchType
  io.op1Select := decoder.op1Select
  io.op2Select := decoder.op2Select
  io.immType := decoder.immType
  io.ALUop := decoder.ALUop

  //*/

  // Drive the PC and instruction as outputs for the barrier inputs
  io.instructionOut := io.instructionIn
  io.PCOut := io.PCIn

  /* Decode the instruction to find the ALU op */








  /*
  def decoder (): Unit = {
    switch(inst.opcode) {
      // R-types
      is("b0110011".U) {
        switch(inst.funct3) {
          // ADD or SUB
          is("b000".U) {
            switch(inst.funct7) {
              // ADD
              is("b0000000".U) {
                inst.instructionType := InstrType.ADD
                inst.aluOp := ALUOps.ADD
              }
              is("b0100000".U) {
                inst.instructionType := InstrType.SUB
                inst.aluOp := ALUOps.SUB
              }
            }
          }
          // AND
          is("b111".U) {
            inst.instructionType := InstrType.AND
            inst.aluOp := ALUOps.AND
          }
          // OR
          is("b110".U) {
            inst.instructionType := InstrType.OR
            inst.aluOp := ALUOps.OR
          }
          // XOR
          is("b100".U) {
            inst.instructionType := InstrType.XOR
            inst.aluOp := ALUOps.XOR
          }
          // SLT
          is("b010".U) {
            inst.instructionType := InstrType.SLT
            inst.aluOp := ALUOps.SLT
          }
          // SLTU
          is("b011".U) {
            inst.instructionType := InstrType.SLTU
            inst.aluOp := ALUOps.SLTU
          }
          // SRA or SRL
          is("b101".U) {
            switch(inst.funct7) {
              // SRA
              is("b0100000".U) {
                inst.instructionType := InstrType.SRA
                inst.aluOp := ALUOps.SRA
              }
              // SRL
              is("b0000000".U) {
                inst.instructionType := InstrType.SRL
                inst.aluOp := ALUOps.SRL
              }
            }
          }
          // SLL
          is("b001".U) {
            inst.instructionType := InstrType.SLL
            inst.aluOp := ALUOps.SLL
          }
        }
      }

      // I-types
      is("b0010011".U) {

      }

    }



  }

  */

}

